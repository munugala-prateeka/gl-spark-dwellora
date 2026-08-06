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
import com.dwellora.exception.BookingException;
import com.dwellora.exception.BookingNotFoundException;
import com.dwellora.kafka.BookingProducer;
import com.dwellora.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BookingServiceImpl}.
 *
 * <p>Covers:
 * <ul>
 *   <li>US-007 (AC-2/AC-3) - PER_FLAT conflict rejection and valid slot boundaries</li>
 *   <li>US-008 - Resident Books an Amenity Slot</li>
 *   <li>US-009 - Resident Cancels a Booking</li>
 *   <li>US-010 - Manager Oversees Bookings</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private AmenityClient amenityClient;

    @Mock
    private BookingProducer bookingProducer;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private UserDTO resident;
    private AmenityDTO amenity;
    private BookingRequestDTO bookingRequest;
    private final LocalDate tomorrow = LocalDate.now().plusDays(1);

    @BeforeEach
    void setUp() {
        resident = new UserDTO();
        resident.setUserId(100);
        resident.setApartmentId(10);
        resident.setFullName("Alice Resident");
        resident.setFlatNumber("A-101");
        resident.setRole("RESIDENT");
        resident.setAccountStatus("ACTIVE");

        amenity = new AmenityDTO();
        amenity.setAmenityId(1);
        amenity.setApartmentId(10);
        amenity.setAmenityName("Gym");
        amenity.setAvailable(true);
        amenity.setOpeningTime(LocalTime.of(6, 0));
        amenity.setClosingTime(LocalTime.of(21, 0));
        amenity.setCapacity(2);
        amenity.setBookingPolicy("PER_PERSON");
        amenity.setSlotDurationMinutes(60);
        amenity.setMaxBookingsPerDay(3);
        amenity.setMaxBookingsPerMonth(null);

        bookingRequest = new BookingRequestDTO(100, 1, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0));
    }

    // ==========================================
    // US-008: BOOK AN AMENITY SLOT
    // ==========================================

    @Test
    @DisplayName("US-008: Given an open slot, when I submit a booking, then it is created with status BOOKED")
    void addBooking_OpenSlot_CreatesBookedReservation() {
        // Given
        when(userClient.getUserById(100)).thenReturn(resident);
        when(amenityClient.getAmenityById(1)).thenReturn(amenity);
        when(bookingRepository.countByUserIdAndAmenityIdAndBookingDateAndBookingStatus(
                100, 1, tomorrow, BookingStatus.BOOKED)).thenReturn(0L);
        when(bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(1, tomorrow, BookingStatus.BOOKED))
                .thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> {
            Booking b = i.getArgument(0);
            b.setBookingId(500);
            return b;
        });

        // When
        BookingResponseDTO response = bookingService.addBooking(bookingRequest);

        // Then
        assertNotNull(response);
        assertEquals(BookingStatus.BOOKED, response.getBookingStatus());
        assertEquals("Gym", response.getAmenityName());
        verify(bookingProducer).sendBookingCreatedEvent(any());
    }

    @Test
    @DisplayName("US-008 (AC-3): Given a slot already at capacity, when I try to book it, then it is rejected with a conflict message")
    void addBooking_SlotFull_ThrowsBookingException() {
        // Given
        Booking existing1 = new Booking(1, 200, 1, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);
        Booking existing2 = new Booking(2, 201, 1, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);

        when(userClient.getUserById(100)).thenReturn(resident);
        when(amenityClient.getAmenityById(1)).thenReturn(amenity);
        when(bookingRepository.countByUserIdAndAmenityIdAndBookingDateAndBookingStatus(
                100, 1, tomorrow, BookingStatus.BOOKED)).thenReturn(0L);
        when(bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(1, tomorrow, BookingStatus.BOOKED))
                .thenReturn(List.of(existing1, existing2)); // capacity is 2, both occupy the same slot

        // When & Then
        BookingException ex = assertThrows(BookingException.class, () -> bookingService.addBooking(bookingRequest));
        assertEquals("Slot is already full.", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-007 (AC-2): Given a PER_FLAT amenity, when another resident from an already-booked flat tries to book the same slot, then it is rejected")
    void addBooking_PerFlatPolicyConflict_ThrowsBookingException() {
        // Given
        amenity.setBookingPolicy("PER_FLAT");
        amenity.setCapacity(5);

        UserDTO flatmate = new UserDTO();
        flatmate.setUserId(101);
        flatmate.setFlatNumber("A-101"); // same flat as `resident`

        Booking flatmateBooking = new Booking(9, 101, 1, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);

        when(userClient.getUserById(100)).thenReturn(resident);
        when(amenityClient.getAmenityById(1)).thenReturn(amenity);
        when(bookingRepository.countByUserIdAndAmenityIdAndBookingDateAndBookingStatus(
                100, 1, tomorrow, BookingStatus.BOOKED)).thenReturn(0L);
        when(bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(1, tomorrow, BookingStatus.BOOKED))
                .thenReturn(List.of(flatmateBooking));
        when(userClient.getUserById(101)).thenReturn(flatmate);

        // When & Then
        BookingException ex = assertThrows(BookingException.class, () -> bookingService.addBooking(bookingRequest));
        assertEquals("This flat has already booked this amenity for the selected date.", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-008: Given a booking date in the past, when submitted, then it is rejected")
    void addBooking_PastDate_ThrowsBookingException() {
        // Given
        BookingRequestDTO pastRequest = new BookingRequestDTO(
                100, 1, LocalDate.now().minusDays(1), LocalTime.of(7, 0), LocalTime.of(8, 0));
        when(userClient.getUserById(100)).thenReturn(resident);
        when(amenityClient.getAmenityById(1)).thenReturn(amenity);

        // When & Then
        assertThrows(BookingException.class, () -> bookingService.addBooking(pastRequest));
    }

    @Test
    @DisplayName("US-008: Given a resident not belonging to the amenity's apartment, when booking, then it is rejected")
    void addBooking_WrongApartment_ThrowsBookingException() {
        // Given
        resident.setApartmentId(999); // different apartment than the amenity's
        when(userClient.getUserById(100)).thenReturn(resident);
        when(amenityClient.getAmenityById(1)).thenReturn(amenity);

        // When & Then
        assertThrows(BookingException.class, () -> bookingService.addBooking(bookingRequest));
    }

    // ==========================================
    // US-009: CANCEL A BOOKING
    // ==========================================

    @Test
    @DisplayName("US-009: Given an active booking, when cancelled, then its status changes to CANCELLED")
    void cancelBooking_ExistingBooking_MarksCancelled() {
        // Given
        Booking booking = new Booking(500, 100, 1, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);
        when(bookingRepository.findById(500)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(amenityClient.getAmenityById(1)).thenReturn(amenity);

        // When
        BookingResponseDTO response = bookingService.cancelBooking(500);

        // Then
        assertEquals(BookingStatus.CANCELLED, response.getBookingStatus());
    }

    @Test
    @DisplayName("US-009: Given a non-existent booking, when cancellation is attempted, then a BookingNotFoundException is thrown")
    void cancelBooking_MissingBooking_ThrowsException() {
        // Given
        when(bookingRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookingNotFoundException.class, () -> bookingService.cancelBooking(999));
    }

    // ==========================================
    // US-008 (AC-1) / availability
    // ==========================================

    @Test
    @DisplayName("US-008 (AC-1): Given an amenity and date, when availability is requested, then only valid slot boundaries are returned")
    void getAvailability_ReturnsSlotsWithinOperatingHours() {
        // Given
        amenity.setOpeningTime(LocalTime.of(6, 0));
        amenity.setClosingTime(LocalTime.of(8, 0));
        amenity.setSlotDurationMinutes(60);
        when(amenityClient.getAmenityById(1)).thenReturn(amenity);
        when(bookingRepository.findByAmenityIdAndBookingDateAndBookingStatus(1, tomorrow, BookingStatus.BOOKED))
                .thenReturn(List.of());

        // When
        List<AvailabilityDTO> slots = bookingService.getAvailability(1, tomorrow);

        // Then: 06:00-07:00 and 07:00-08:00, nothing beyond closing time
        assertEquals(2, slots.size());
        assertEquals(0L, slots.get(0).getBooked());
        assertEquals(2L, slots.get(0).getRemaining());
        assertEquals(amenity.getCapacity(), slots.get(0).getCapacity());
    }

    // ==========================================
    // US-010: MANAGER OVERSIGHT
    // ==========================================

    @Test
    @DisplayName("US-010: Given an apartment id, when bookings are requested, then a consolidated list across amenities and residents is returned")
    void getBookingsByApartment_ReturnsOnlyThatApartmentsBookings() {
        // Given
        Booking inApartment = new Booking(1, 100, 1, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);

        AmenityDTO otherAmenity = new AmenityDTO();
        otherAmenity.setAmenityId(2);
        otherAmenity.setApartmentId(20); // different apartment
        otherAmenity.setAmenityName("Pool");
        Booking outOfApartment = new Booking(2, 200, 2, tomorrow, LocalTime.of(9, 0), LocalTime.of(10, 0), BookingStatus.BOOKED);

        when(bookingRepository.findAll()).thenReturn(List.of(inApartment, outOfApartment));
        when(amenityClient.getAmenityById(1)).thenReturn(amenity);
        when(amenityClient.getAmenityById(2)).thenReturn(otherAmenity);
        when(userClient.getUserById(100)).thenReturn(resident);

        // When
        List<AdminBookingDTO> result = bookingService.getBookingsByApartment(10);

        // Then
        assertEquals(1, result.size());
        assertEquals("Alice Resident", result.get(0).getResidentName());
    }

    @Test
    @DisplayName("US-010: Given today's date, when the booking count is requested, then an accurate count scoped to the apartment is returned")
    void getTodayBookingCount_ReturnsScopedCount() {
        // Given
        Booking today1 = new Booking(1, 100, 1, LocalDate.now(), LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);
        when(bookingRepository.findByBookingDate(LocalDate.now())).thenReturn(List.of(today1));
        when(amenityClient.getAmenityById(1)).thenReturn(amenity);

        // When
        Long count = bookingService.getTodayBookingCount(10);

        // Then
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("US-010: Given a resident id, when their booking history is requested, then all of their bookings are returned")
    void getBookingsByUser_ReturnsUsersHistory() {
        // Given
        Booking b1 = new Booking(1, 100, 1, tomorrow, LocalTime.of(7, 0), LocalTime.of(8, 0), BookingStatus.BOOKED);
        Booking b2 = new Booking(2, 100, 1, tomorrow.minusDays(2), LocalTime.of(9, 0), LocalTime.of(10, 0), BookingStatus.CANCELLED);
        when(bookingRepository.findByUserId(100)).thenReturn(List.of(b1, b2));
        when(amenityClient.getAmenityById(1)).thenReturn(amenity);

        // When
        List<BookingResponseDTO> history = bookingService.getBookingsByUser(100);

        // Then
        assertEquals(2, history.size());
        assertFalse(history.isEmpty());
    }
}
