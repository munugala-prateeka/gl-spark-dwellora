package com.dwellora.service.impl;

import com.dwellora.client.AmenityClient;
import com.dwellora.client.UserClient;
import com.dwellora.dto.AdminBookingDTO;
import com.dwellora.dto.AmenityDTO;
import com.dwellora.dto.AvailabilityDTO;
import com.dwellora.dto.BookingRequestDTO;
import com.dwellora.dto.BookingResponseDTO;
import com.dwellora.dto.UserDTO;
import com.dwellora.entity.Booking;
import com.dwellora.enums.BookingStatus;
import com.dwellora.event.BookingCreatedEvent;
import com.dwellora.exception.BookingException;
import com.dwellora.exception.BookingNotFoundException;
import com.dwellora.kafka.BookingProducer;
import com.dwellora.repository.BookingRepository;
import com.dwellora.service.BookingService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing amenity bookings within the Dwellora application.
 * Handles creation, retrieval, cancellation, and validation of user amenity reservations.
 */
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserClient userClient;
    private final AmenityClient amenityClient;
    private final BookingProducer bookingProducer;

    /**
     * Constructs a new BookingServiceImpl instance with required repositories and clients.
     */
    public BookingServiceImpl(
            BookingRepository bookingRepository,
            UserClient userClient,
            AmenityClient amenityClient,
            BookingProducer bookingProducer) {
        this.bookingRepository = bookingRepository;
        this.userClient = userClient;
        this.amenityClient = amenityClient;
        this.bookingProducer = bookingProducer;
    }

    /**
     * Creates a new booking for a resident after validating user, amenity, and slot availability.
     * Publishes a booking created event upon successful creation.
     */
    @Override
    @Transactional
    public BookingResponseDTO addBooking(Long userId, BookingRequestDTO dto) {
        if (userId == null) {
            throw new BookingException("User ID is required.");
        }

        validateBookingRequest(dto);
        UserDTO user;
        try {
            user = userClient.getUserById(userId);
        } catch (Exception e) {
            throw new BookingException("Unable to fetch user details. User ID: " + userId);
        }

        if (user == null) {
            throw new BookingNotFoundException("User not found with ID: " + userId);
        }

        if (!"RESIDENT".equalsIgnoreCase(user.getRole())) {
            throw new BookingException("Only residents can book amenities.");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
            throw new BookingException("Resident account is not active.");
        }

        if (user.getApartmentId() == null) {
            throw new BookingException("Resident is not associated with an apartment.");
        }

        AmenityDTO amenity;
        try {
            amenity = amenityClient.getAmenityById(dto.getAmenityId());
        } catch (Exception e) {
            throw new BookingException(
                    "Unable to fetch amenity details. Amenity ID: " + dto.getAmenityId());
        }

        if (amenity == null) {
            throw new BookingNotFoundException("Amenity not found with ID: " + dto.getAmenityId());
        }

        validateAmenity(amenity);

        if (Boolean.FALSE.equals(amenity.getAvailable())) {
            throw new BookingException("Amenity is currently unavailable.");
        }

        if (!user.getApartmentId().equals(amenity.getApartmentId())) {
            throw new BookingException("Resident belongs to another apartment.");
        }

        validateDateAndTime(dto, amenity);

        if (amenity.getMaxBookingsPerDay() != null && amenity.getMaxBookingsPerDay() > 0) {
            long todayBookings =
                    bookingRepository.countByUserIdAndAmenityIdAndBookingDateAndBookingStatus(
                            userId, dto.getAmenityId(), dto.getBookingDate(), BookingStatus.BOOKED);

            if (todayBookings >= amenity.getMaxBookingsPerDay()) {
                throw new BookingException("Maximum bookings reached for this amenity today.");
            }
        }

        if (amenity.getMaxBookingsPerMonth() != null && amenity.getMaxBookingsPerMonth() > 0) {
            LocalDate monthStart = dto.getBookingDate().withDayOfMonth(1);
            LocalDate monthEnd =
                    dto.getBookingDate().withDayOfMonth(dto.getBookingDate().lengthOfMonth());

            long monthlyBookings =
                    bookingRepository.countByUserIdAndAmenityIdAndBookingDateBetweenAndBookingStatus(
                            userId, dto.getAmenityId(), monthStart, monthEnd, BookingStatus.BOOKED);

            if (monthlyBookings >= amenity.getMaxBookingsPerMonth()) {
                throw new BookingException("Maximum monthly bookings reached for this amenity.");
            }
        }

        List<Booking> bookings =
                bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(
                        dto.getAmenityId(), dto.getBookingDate(), BookingStatus.BOOKED);

        long occupied =
                bookings.stream()
                        .filter(
                                b ->
                                        b.getStartTime().equals(dto.getStartTime())
                                                && b.getEndTime().equals(dto.getEndTime()))
                        .count();

        if (occupied >= amenity.getCapacity()) {
            throw new BookingException("Selected slot is already full.");
        }

        if ("PER_PERSON".equalsIgnoreCase(amenity.getBookingPolicy())) {
            boolean alreadyBooked =
                    bookings.stream()
                            .anyMatch(
                                    b ->
                                            b.getUserId().equals(userId)
                                                    && b.getStartTime().equals(dto.getStartTime())
                                                    && b.getEndTime().equals(dto.getEndTime()));

            if (alreadyBooked) {
                throw new BookingException("You have already booked this slot.");
            }
        }

        if ("PER_FLAT".equalsIgnoreCase(amenity.getBookingPolicy())) {
            if (user.getFlatNumber() == null) {
                throw new BookingException("Resident flat number is not available.");
            }

            for (Booking existingBooking : bookings) {
                UserDTO bookedUser;
                try {
                    bookedUser = userClient.getUserById(existingBooking.getUserId());
                } catch (Exception e) {
                    continue;
                }

                if (bookedUser != null
                        && bookedUser.getFlatNumber() != null
                        && user.getFlatNumber().equalsIgnoreCase(bookedUser.getFlatNumber())) {
                    throw new BookingException(
                            "This flat has already booked this amenity for the selected date.");
                }
            }
        }

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setAmenityId(dto.getAmenityId());
        booking.setApartmentId(amenity.getApartmentId());
        booking.setBookingDate(dto.getBookingDate());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setBookingStatus(BookingStatus.BOOKED);

        Booking savedBooking = bookingRepository.save(booking);

        BookingCreatedEvent event =
                new BookingCreatedEvent(
                        savedBooking.getBookingId(),
                        savedBooking.getUserId(),
                        savedBooking.getAmenityId(),
                        amenity.getAmenityName(),
                        savedBooking.getBookingDate(),
                        savedBooking.getStartTime(),
                        savedBooking.getEndTime());

        bookingProducer.sendBookingCreatedEvent(event);

        return mapToResponseDTO(savedBooking, amenity.getAmenityName());
    }

    /**
     * Retrieves all bookings across the system with populated amenity details.
     */
    @Override
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToResponseDTOWithClient)
                .toList();
    }

    /**
     * Retrieves specific booking details by its unique identifier.
     */
    @Override
    public BookingResponseDTO getBookingById(Long bookingId) {
        Booking booking =
                bookingRepository
                        .findById(bookingId)
                        .orElseThrow(
                                () -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        return mapToResponseDTOWithClient(booking);
    }

    /**
     * Retrieves all bookings scheduled for a given date.
     */
    @Override
    public List<BookingResponseDTO> getBookingsByDate(LocalDate bookingDate) {
        if (bookingDate == null) {
            throw new BookingException("Booking date is required.");
        }

        return bookingRepository.findByBookingDate(bookingDate).stream()
                .map(this::mapToResponseDTOWithClient)
                .toList();
    }

    /**
     * Cancels an existing booking if authorized and the booking is not in the past.
     */
    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long bookingId, Long userId) {
        Booking booking =
                bookingRepository
                        .findById(bookingId)
                        .orElseThrow(
                                () -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUserId().equals(userId)) {
            throw new BookingException("You are not authorized to cancel this booking.");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled.");
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (booking.getBookingDate().isBefore(today)
                || (booking.getBookingDate().equals(today) && booking.getEndTime().isBefore(now))) {
            throw new BookingException("Past bookings cannot be cancelled.");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);

        return mapToResponseDTOWithClient(savedBooking);
    }

    /**
     * Retrieves all bookings associated with a specific user.
     */
    @Override
    public List<BookingResponseDTO> getBookingsByUser(Long userId) {
        if (userId == null) {
            throw new BookingException("User ID is required.");
        }

        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTOWithClient)
                .toList();
    }

    /**
     * Calculates time-slot availability and remaining capacity for an amenity on a specific date.
     */
    @Override
    public List<AvailabilityDTO> getAvailability(Long amenityId, LocalDate bookingDate) {
        if (bookingDate == null) {
            throw new BookingException("Booking date is required.");
        }

        if (bookingDate.isBefore(LocalDate.now())) {
            throw new BookingException("Availability cannot be checked for a past date.");
        }

        AmenityDTO amenity;
        try {
            amenity = amenityClient.getAmenityById(amenityId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BookingException(
                    "Amenity Feign call failed: "
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage());
        }

        if (amenity == null) {
            throw new BookingNotFoundException("Amenity not found with ID: " + amenityId);
        }

        validateAmenity(amenity);

        List<AvailabilityDTO> availability = new ArrayList<>();
        LocalTime slotStart = amenity.getOpeningTime();

        List<Booking> activeBookings =
                bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(
                        amenityId, bookingDate, BookingStatus.BOOKED);

        while (slotStart.isBefore(amenity.getClosingTime())) {
            LocalTime slotEnd = slotStart.plusMinutes(amenity.getSlotDurationMinutes());

            if (slotEnd.isAfter(amenity.getClosingTime())) {
                break;
            }

            LocalTime currentStart = slotStart;
            LocalTime currentEnd = slotEnd;

            long occupied =
                    activeBookings.stream()
                            .filter(
                                    b ->
                                            b.getStartTime().equals(currentStart)
                                                    && b.getEndTime().equals(currentEnd))
                            .count();

            long remaining = Math.max(0L, amenity.getCapacity() - occupied);

            availability.add(
                    new AvailabilityDTO(
                            currentStart + " - " + currentEnd,
                            amenity.getCapacity(),
                            occupied,
                            remaining,
                            amenity.getBookingPolicy()));

            slotStart = slotEnd;
        }

        return availability;
    }

    /**
     * Retrieves all bookings under a specific apartment complex for administrative review.
     */
    @Override
    public List<AdminBookingDTO> getBookingsByApartment(Long apartmentId) {
        List<Booking> bookings = bookingRepository.findByApartmentId(apartmentId);
        List<AdminBookingDTO> result = new ArrayList<>();

        for (Booking booking : bookings) {
            String amenityName = "Unknown Amenity";
            try {
                AmenityDTO amenity = amenityClient.getAmenityById(booking.getAmenityId());
                if (amenity != null) {
                    amenityName = amenity.getAmenityName();
                }
            } catch (Exception ignored) {
            }

            UserDTO user = null;
            try {
                user = userClient.getUserById(booking.getUserId());
            } catch (Exception ignored) {
            }

            result.add(
                    new AdminBookingDTO(
                            booking.getBookingId(),
                            user != null ? user.getFullName() : "Unknown",
                            user != null ? user.getFlatNumber() : "N/A",
                            amenityName,
                            booking.getApartmentId(),
                            booking.getBookingDate(),
                            booking.getStartTime(),
                            booking.getEndTime(),
                            booking.getBookingStatus()));
        }

        return result;
    }

    /**
     * Counts the total number of active bookings made today for an apartment complex.
     */
    @Override
    public Long getTodayBookingCount(Long apartmentId) {
        return bookingRepository.countByApartmentIdAndBookingDateAndBookingStatus(
                apartmentId, LocalDate.now(), BookingStatus.BOOKED);
    }

    private void validateBookingRequest(BookingRequestDTO dto) {
        if (dto == null) {
            throw new BookingException("Booking request cannot be null.");
        }

        if (dto.getAmenityId() == null) {
            throw new BookingException("Amenity ID is required.");
        }

        if (dto.getBookingDate() == null) {
            throw new BookingException("Booking date is required.");
        }

        if (dto.getStartTime() == null) {
            throw new BookingException("Start time is required.");
        }

        if (dto.getEndTime() == null) {
            throw new BookingException("End time is required.");
        }

        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new BookingException("Start time must be before end time.");
        }

        if (dto.getBookingDate().isBefore(LocalDate.now())) {
            throw new BookingException("Past dates cannot be booked.");
        }
    }

    private void validateAmenity(AmenityDTO amenity) {
        if (amenity.getApartmentId() == null) {
            throw new BookingException("Amenity is not associated with an apartment.");
        }

        if (amenity.getCapacity() == null || amenity.getCapacity() <= 0) {
            throw new BookingException("Amenity capacity is invalid.");
        }

        if (amenity.getOpeningTime() == null || amenity.getClosingTime() == null) {
            throw new BookingException("Amenity operating hours are not configured.");
        }

        if (!amenity.getOpeningTime().isBefore(amenity.getClosingTime())) {
            throw new BookingException("Amenity operating hours are invalid.");
        }

        if (amenity.getSlotDurationMinutes() == null || amenity.getSlotDurationMinutes() <= 0) {
            throw new BookingException("Amenity slot duration is invalid.");
        }

        if (amenity.getBookingPolicy() == null
                || (!"PER_PERSON".equalsIgnoreCase(amenity.getBookingPolicy())
                && !"PER_FLAT".equalsIgnoreCase(amenity.getBookingPolicy()))) {
            throw new BookingException("Amenity booking policy is invalid.");
        }
    }

    private void validateDateAndTime(BookingRequestDTO dto, AmenityDTO amenity) {
        LocalDate bookingDate = dto.getBookingDate();
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime = dto.getEndTime();

        if (bookingDate.isBefore(LocalDate.now())) {
            throw new BookingException("Past dates cannot be booked.");
        }

        if (bookingDate.equals(LocalDate.now()) && !startTime.isAfter(LocalTime.now())) {
            throw new BookingException("The selected time has already started.");
        }

        if (startTime.isBefore(amenity.getOpeningTime())) {
            throw new BookingException("Booking starts before amenity opening time.");
        }

        if (endTime.isAfter(amenity.getClosingTime())) {
            throw new BookingException("Booking ends after amenity closing time.");
        }

        long durationMinutes = Duration.between(startTime, endTime).toMinutes();

        if (durationMinutes != amenity.getSlotDurationMinutes()) {
            throw new BookingException(
                    "Booking duration must be exactly " + amenity.getSlotDurationMinutes() + " minutes.");
        }

        long minutesFromOpening = Duration.between(amenity.getOpeningTime(), startTime).toMinutes();

        if (minutesFromOpening % amenity.getSlotDurationMinutes() != 0) {
            throw new BookingException("Selected time does not match an available slot.");
        }
    }

    private BookingResponseDTO mapToResponseDTOWithClient(Booking booking) {
        String amenityName = "Unknown";

        try {
            AmenityDTO amenity = amenityClient.getAmenityById(booking.getAmenityId());
            if (amenity != null && amenity.getAmenityName() != null) {
                amenityName = amenity.getAmenityName();
            }
        } catch (Exception ignored) {
        }

        return mapToResponseDTO(booking, amenityName);
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking, String amenityName) {
        return new BookingResponseDTO(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getAmenityId(),
                amenityName,
                booking.getApartmentId(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBookingStatus());
    }
}