package com.dwellora.service.impl;

import com.dwellora.client.AmenityClient;
import com.dwellora.client.UserClient;
import com.dwellora.dto.*;
import com.dwellora.entity.Booking;
import com.dwellora.enums.BookingStatus;
import com.dwellora.event.BookingCreatedEvent;
import com.dwellora.exception.BookingException;
import com.dwellora.exception.BookingNotFoundException;
import com.dwellora.kafka.BookingProducer;
import com.dwellora.repository.BookingRepository;
import com.dwellora.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserClient userClient;
    private final AmenityClient amenityClient;
    private final BookingProducer bookingProducer;

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

    @Override
    @Transactional
    public BookingResponseDTO addBooking(BookingRequestDTO dto) {

        UserDTO user;
        try {
            user = userClient.getUserById(dto.getUserId());
        } catch (Exception e) {
            throw new BookingException("Unable to fetch user details. User ID: " + dto.getUserId());
        }

        if (user == null) {
            throw new BookingNotFoundException("User not found with ID: " + dto.getUserId());
        }

        AmenityDTO amenity;
        try {
            amenity = amenityClient.getAmenityById(dto.getAmenityId());
        } catch (Exception e) {
            throw new BookingException("Unable to fetch amenity details. Amenity ID: " + dto.getAmenityId());
        }

        if (amenity == null) {
            throw new BookingNotFoundException("Amenity not found with ID: " + dto.getAmenityId());
        }

        if (dto.getBookingDate().isBefore(LocalDate.now())) {
            throw new BookingException("Past dates cannot be booked.");
        }

        if (Boolean.FALSE.equals(amenity.getAvailable())) {
            throw new BookingException("Amenity is currently unavailable.");
        }

        if (!"RESIDENT".equalsIgnoreCase(user.getRole())) {
            throw new BookingException("Only residents can book amenities.");
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
            throw new BookingException("Resident account is not active.");
        }

        if (!user.getApartmentId().equals(amenity.getApartmentId())) {
            throw new BookingException("Resident belongs to another apartment.");
        }

        if (dto.getStartTime().isBefore(amenity.getOpeningTime())
                || dto.getEndTime().isAfter(amenity.getClosingTime())) {
            throw new BookingException("Booking outside operating hours.");
        }

        long todayBookings = bookingRepository.countByUserIdAndAmenityIdAndBookingDateAndBookingStatus(
                dto.getUserId(),
                dto.getAmenityId(),
                dto.getBookingDate(),
                BookingStatus.BOOKED
        );

        if (todayBookings >= amenity.getMaxBookingsPerDay()) {
            throw new BookingException("Maximum bookings reached for today.");
        }

        if (amenity.getMaxBookingsPerMonth() != null) {
            LocalDate monthStart = dto.getBookingDate().withDayOfMonth(1);
            LocalDate monthEnd = dto.getBookingDate().withDayOfMonth(dto.getBookingDate().lengthOfMonth());

            long monthlyBookings = bookingRepository.countByUserIdAndAmenityIdAndBookingDateBetweenAndBookingStatus(
                    dto.getUserId(),
                    dto.getAmenityId(),
                    monthStart,
                    monthEnd,
                    BookingStatus.BOOKED
            );

            if (monthlyBookings >= amenity.getMaxBookingsPerMonth()) {
                throw new BookingException("Maximum monthly bookings reached for this amenity.");
            }
        }

        List<Booking> bookings = bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(
                dto.getAmenityId(),
                dto.getBookingDate(),
                BookingStatus.BOOKED
        );

        long occupied = bookings.stream()
                .filter(b -> b.getStartTime().equals(dto.getStartTime())
                        && b.getEndTime().equals(dto.getEndTime()))
                .count();

        if (occupied >= amenity.getCapacity()) {
            throw new BookingException("Slot is already full.");
        }

        if ("PER_PERSON".equals(amenity.getBookingPolicy())) {
            boolean alreadyBooked = bookings.stream().anyMatch(b ->
                    b.getUserId().equals(dto.getUserId())
                            && b.getStartTime().equals(dto.getStartTime())
                            && b.getEndTime().equals(dto.getEndTime())
            );

            if (alreadyBooked) {
                throw new BookingException("User already booked this slot.");
            }
        }

        if ("PER_FLAT".equals(amenity.getBookingPolicy())) {
            for (Booking existing : bookings) {
                UserDTO bookedUser = userClient.getUserById(existing.getUserId());
                if (bookedUser != null && user.getFlatNumber().equals(bookedUser.getFlatNumber())) {
                    throw new BookingException("This flat has already booked this amenity for the selected date.");
                }
            }
        }

        Booking booking = new Booking();
        booking.setUserId(dto.getUserId());
        booking.setAmenityId(dto.getAmenityId());
        booking.setBookingDate(dto.getBookingDate());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setBookingStatus(BookingStatus.BOOKED);

        Booking savedBooking = bookingRepository.save(booking);

        BookingCreatedEvent event = new BookingCreatedEvent(
                savedBooking.getBookingId(),
                savedBooking.getUserId(),
                savedBooking.getAmenityId(),
                amenity.getAmenityName(),
                savedBooking.getBookingDate(),
                savedBooking.getStartTime(),
                savedBooking.getEndTime()
        );

        bookingProducer.sendBookingCreatedEvent(event);

        return mapToResponseDTO(savedBooking, amenity.getAmenityName());
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToResponseDTOWithClient)
                .toList();
    }

    @Override
    public BookingResponseDTO getBookingById(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        return mapToResponseDTOWithClient(booking);
    }

    @Override
    public List<BookingResponseDTO> getBookingsByDate(LocalDate bookingDate) {
        return bookingRepository.findByBookingDate(bookingDate).stream()
                .map(this::mapToResponseDTOWithClient)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBooking(Integer bookingId, BookingRequestDTO dto) {
        Booking existing = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        existing.setUserId(dto.getUserId());
        existing.setAmenityId(dto.getAmenityId());
        existing.setBookingDate(dto.getBookingDate());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());

        Booking saved = bookingRepository.save(existing);
        return mapToResponseDTOWithClient(saved);
    }

    @Override
    public List<BookingResponseDTO> getBookingsByUser(Integer userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        return bookings.stream().map(this::mapToResponseDTOWithClient).toList();
    }

    @Override
    public List<AvailabilityDTO> getAvailability(Integer amenityId, LocalDate bookingDate) {
        AmenityDTO amenity = amenityClient.getAmenityById(amenityId);
        if (amenity == null) {
            throw new BookingNotFoundException("Amenity not found with ID: " + amenityId);
        }

        List<AvailabilityDTO> list = new ArrayList<>();
        LocalTime slotStart = amenity.getOpeningTime();

        List<Booking> activeBookings = bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(
                amenityId, bookingDate, BookingStatus.BOOKED);

        while (slotStart.isBefore(amenity.getClosingTime())) {
            LocalTime slotEnd = slotStart.plusMinutes(amenity.getSlotDurationMinutes());

            if (slotEnd.isBefore(slotStart) || slotEnd.isAfter(amenity.getClosingTime())) {
                break;
            }

            final LocalTime currentStart = slotStart;
            final LocalTime currentEnd = slotEnd;

            long occupied = activeBookings.stream()
                    .filter(b -> b.getStartTime().equals(currentStart) && b.getEndTime().equals(currentEnd))
                    .count();

            list.add(new AvailabilityDTO(
                    slotStart + " - " + slotEnd,
                    amenity.getCapacity(),
                    occupied,
                    amenity.getCapacity() - occupied,
                    amenity.getBookingPolicy()
            ));

            slotStart = slotEnd;
        }

        return list;
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        return mapToResponseDTOWithClient(saved);
    }

    private BookingResponseDTO mapToResponseDTOWithClient(Booking booking) {
        String amenityName = "Unknown";
        try {
            AmenityDTO amenity = amenityClient.getAmenityById(booking.getAmenityId());
            if (amenity != null) {
                amenityName = amenity.getAmenityName();
            }
        } catch (Exception e) {
            // Logged automatically by LoggingAspect
        }
        return mapToResponseDTO(booking, amenityName);
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking, String amenityName) {
        return new BookingResponseDTO(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getAmenityId(),
                amenityName,
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBookingStatus()
        );
    }
}