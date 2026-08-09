package com.dwellora.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.dwellora.exception.BookingException;
import com.dwellora.exception.BookingNotFoundException;
import com.dwellora.kafka.BookingProducer;
import com.dwellora.repository.BookingRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link BookingServiceImpl}. Covers slot bookings, cancellations, availability checks,
 * and manager oversight.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;

    @Mock private UserClient userClient;

    @Mock private AmenityClient amenityClient;

    @Mock private BookingProducer bookingProducer;

    @InjectMocks private BookingServiceImpl bookingService;

    private UserDTO resident;
    private AmenityDTO amenity;
    private BookingRequestDTO bookingRequest;
    private final LocalDate tomorrow = LocalDate.now().plusDays(1);

    /** Sets up test data before each test execution. */
    @BeforeEach
    void setUp() {
        resident = new UserDTO();
        resident.setUserId(100L);
        resident.setApartmentId(10L);
        resident.setFullName("Alice Resident");
        resident.setFlatNumber("A-101");
        resident.setRole("RESIDENT");
        resident.setAccountStatus("ACTIVE");

        amenity = new AmenityDTO();
        amenity.setAmenityId(1L);
        amenity.setApartmentId(10L);
        amenity.setAmenityName("Gym");
        amenity.setAvailable(true);
        amenity.setOpeningTime(LocalTime.of(6, 0));
        amenity.setClosingTime(LocalTime.of(21, 0));
        amenity.setCapacity(2);
        amenity.setBookingPolicy("PER_PERSON");
        amenity.setSlotDurationMinutes(60);
        amenity.setMaxBookingsPerDay(3);
        amenity.setMaxBookingsPerMonth(null);

        bookingRequest = new BookingRequestDTO(1L, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0));
    }

    // ==========================================
    // US-008: BOOK AN AMENITY SLOT
    // ==========================================

    /** Tests successful creation of a booking given an available open slot. */
    @Test
    @DisplayName(
            "US-008: Given an open slot, when I submit a booking, then it is created with status BOOKED")
    void addBooking_OpenSlot_CreatesBookedReservation() {
        when(userClient.getUserById(100L)).thenReturn(resident);
        when(amenityClient.getAmenityById(1L)).thenReturn(amenity);
        when(bookingRepository.countByUserIdAndAmenityIdAndBookingDateAndBookingStatus(
                100L, 1L, tomorrow, BookingStatus.BOOKED))
                .thenReturn(0L);
        when(bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(
                1L, tomorrow, BookingStatus.BOOKED))
                .thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        i -> {
                            Booking b = i.getArgument(0);
                            b.setBookingId(500L);
                            return b;
                        });

        BookingResponseDTO response = bookingService.addBooking(100L, bookingRequest);

        assertNotNull(response);
        assertEquals(BookingStatus.BOOKED, response.getBookingStatus());
        assertEquals("Gym", response.getAmenityName());
        verify(bookingProducer).sendBookingCreatedEvent(any());
    }

    /** Tests rejection when attempting to book a slot that has reached maximum capacity. */
    @Test
    @DisplayName(
            "US-008 (AC-3): Given a slot already at capacity, when I try to book it, then it is"
                    + " rejected with a conflict message")
    void addBooking_SlotFull_ThrowsBookingException() {
        Booking existing1 =
                new Booking(1L, 200L, 1L, 10L, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);
        Booking existing2 =
                new Booking(2L, 201L, 1L, 10L, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);

        when(userClient.getUserById(100L)).thenReturn(resident);
        when(amenityClient.getAmenityById(1L)).thenReturn(amenity);
        when(bookingRepository.countByUserIdAndAmenityIdAndBookingDateAndBookingStatus(
                100L, 1L, tomorrow, BookingStatus.BOOKED))
                .thenReturn(0L);
        when(bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(
                1L, tomorrow, BookingStatus.BOOKED))
                .thenReturn(List.of(existing1, existing2));

        BookingException ex =
                assertThrows(BookingException.class, () -> bookingService.addBooking(100L, bookingRequest));
        assertEquals("Selected slot is already full.", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    /** Tests conflict rejection under PER_FLAT booking policy when flatmate has already booked. */
    @Test
    @DisplayName(
            "US-007 (AC-2): Given a PER_FLAT amenity, when another resident from an already-booked flat"
                    + " tries to book the same slot, then it is rejected")
    void addBooking_PerFlatPolicyConflict_ThrowsBookingException() {
        amenity.setBookingPolicy("PER_FLAT");
        amenity.setCapacity(5);

        UserDTO flatmate = new UserDTO();
        flatmate.setUserId(101L);
        flatmate.setFlatNumber("A-101");

        Booking flatmateBooking =
                new Booking(9L, 101L, 1L, 10L, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);

        when(userClient.getUserById(100L)).thenReturn(resident);
        when(amenityClient.getAmenityById(1L)).thenReturn(amenity);
        when(bookingRepository.countByUserIdAndAmenityIdAndBookingDateAndBookingStatus(
                100L, 1L, tomorrow, BookingStatus.BOOKED))
                .thenReturn(0L);
        when(bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(
                1L, tomorrow, BookingStatus.BOOKED))
                .thenReturn(List.of(flatmateBooking));
        when(userClient.getUserById(101L)).thenReturn(flatmate);

        BookingException ex =
                assertThrows(BookingException.class, () -> bookingService.addBooking(100L, bookingRequest));
        assertEquals(
                "This flat has already booked this amenity for the selected date.", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    /** Tests rejection when attempting to create a booking for a past date. */
    @Test
    @DisplayName("US-008: Given a booking date in the past, when submitted, then it is rejected")
    void addBooking_PastDate_ThrowsBookingException() {
        BookingRequestDTO pastRequest =
                new BookingRequestDTO(1L, LocalDate.now().minusDays(1), LocalTime.of(7, 0), LocalTime.of(8, 0));

        assertThrows(BookingException.class, () -> bookingService.addBooking(100L, pastRequest));
    }

    /** Tests rejection when resident tries to book an amenity belonging to a different apartment. */
    @Test
    @DisplayName(
            "US-008: Given a resident not belonging to the amenity's apartment, when booking, then it is"
                    + " rejected")
    void addBooking_WrongApartment_ThrowsBookingException() {
        resident.setApartmentId(999L);
        when(userClient.getUserById(100L)).thenReturn(resident);
        when(amenityClient.getAmenityById(1L)).thenReturn(amenity);

        assertThrows(BookingException.class, () -> bookingService.addBooking(100L, bookingRequest));
    }

    // ==========================================
    // US-009: CANCEL A BOOKING
    // ==========================================

    /** Tests successfully cancelling an active booking. */
    @Test
    @DisplayName(
            "US-009: Given an active booking, when cancelled, then its status changes to CANCELLED")
    void cancelBooking_ExistingBooking_MarksCancelled() {
        Booking booking =
                new Booking(500L, 100L, 1L, 10L, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);
        when(bookingRepository.findById(500L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(amenityClient.getAmenityById(1L)).thenReturn(amenity);

        BookingResponseDTO response = bookingService.cancelBooking(500L, 100L);

        assertEquals(BookingStatus.CANCELLED, response.getBookingStatus());
    }

    /** Tests throwing BookingNotFoundException when attempting to cancel a non-existent booking. */
    @Test
    @DisplayName(
            "US-009: Given a non-existent booking, when cancellation is attempted, then a"
                    + " BookingNotFoundException is thrown")
    void cancelBooking_MissingBooking_ThrowsException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class, () -> bookingService.cancelBooking(999L, 100L));
    }

    // ==========================================
    // US-008 (AC-1) / availability
    // ==========================================

    /** Tests fetching slot availability within defined operating hours. */
    @Test
    @DisplayName(
            "US-008 (AC-1): Given an amenity and date, when availability is requested, then only valid"
                    + " slot boundaries are returned")
    void getAvailability_ReturnsSlotsWithinOperatingHours() {
        amenity.setOpeningTime(LocalTime.of(6, 0));
        amenity.setClosingTime(LocalTime.of(8, 0));
        amenity.setSlotDurationMinutes(60);
        when(amenityClient.getAmenityById(1L)).thenReturn(amenity);
        when(bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(
                1L, tomorrow, BookingStatus.BOOKED))
                .thenReturn(List.of());

        List<AvailabilityDTO> slots = bookingService.getAvailability(1L, tomorrow);

        assertEquals(2, slots.size());
        assertEquals(0L, slots.get(0).getBooked());
        assertEquals(2L, slots.get(0).getRemaining());
        assertEquals(amenity.getCapacity(), slots.get(0).getCapacity());
    }

    // ==========================================
    // US-010: MANAGER OVERSIGHT
    // ==========================================

    /** Tests fetching bookings scoped to a specific apartment complex for admin oversight. */
    @Test
    @DisplayName(
            "US-010: Given an apartment id, when bookings are requested, then a consolidated list across"
                    + " amenities and residents is returned")
    void getBookingsByApartment_ReturnsOnlyThatApartmentsBookings() {
        Booking inApartment =
                new Booking(1L, 100L, 1L, 10L, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);

        when(bookingRepository.findByApartmentId(10L)).thenReturn(List.of(inApartment));
        when(amenityClient.getAmenityById(1L)).thenReturn(amenity);
        when(userClient.getUserById(100L)).thenReturn(resident);

        List<AdminBookingDTO> result = bookingService.getBookingsByApartment(10L);

        assertEquals(1, result.size());
        assertEquals("Alice Resident", result.get(0).getResidentName());
    }

    /** Tests retrieving accurate count of active bookings for today for a given apartment. */
    @Test
    @DisplayName(
            "US-010: Given today's date, when the booking count is requested, then an accurate count"
                    + " scoped to the apartment is returned")
    void getTodayBookingCount_ReturnsScopedCount() {
        when(bookingRepository.countByApartmentIdAndBookingDateAndBookingStatus(
                10L, LocalDate.now(), BookingStatus.BOOKED))
                .thenReturn(1L);

        Long count = bookingService.getTodayBookingCount(10L);

        assertEquals(1L, count);
    }

    /** Tests retrieving complete booking history for a given user. */
    @Test
    @DisplayName(
            "US-010: Given a resident id, when their booking history is requested, then all of their"
                    + " bookings are returned")
    void getBookingsByUser_ReturnsUsersHistory() {
        Booking b1 =
                new Booking(1L, 100L, 1L, 10L, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);
        Booking b2 =
                new Booking(
                        2L, 100L, 1L, 10L, tomorrow.minusDays(2), LocalTime.of(9, 0), LocalTime.of(10, 0), BookingStatus.CANCELLED);
        when(bookingRepository.findByUserId(100L)).thenReturn(List.of(b1, b2));
        when(amenityClient.getAmenityById(1L)).thenReturn(amenity);

        List<BookingResponseDTO> history = bookingService.getBookingsByUser(100L);

        assertEquals(2, history.size());
        assertFalse(history.isEmpty());
    }
}