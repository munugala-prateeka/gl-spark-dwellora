package com.dwellora.service;

import com.dwellora.dto.AdminBookingDTO;
import com.dwellora.dto.AvailabilityDTO;
import com.dwellora.dto.BookingRequestDTO;
import com.dwellora.dto.BookingResponseDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface defining core operations for managing amenity bookings.
 * Provides methods for booking creation, retrieval, cancellation, and availability checks.
 */
public interface BookingService {

    /**
     * Creates a new amenity booking for a specified user.
     */
    BookingResponseDTO addBooking(Long userId, BookingRequestDTO bookingRequestDTO);

    /**
     * Retrieves all existing bookings across the system.
     */
    List<BookingResponseDTO> getAllBookings();

    /**
     * Retrieves a single booking by its unique identifier.
     */
    BookingResponseDTO getBookingById(Long id);

    /**
     * Retrieves all bookings scheduled for a specific date.
     */
    List<BookingResponseDTO> getBookingsByDate(LocalDate bookingDate);

    // BookingResponseDTO updateBooking(Long id, BookingRequestDTO bookingRequestDTO);
    // void deleteBooking(Long id);

    /**
     * Cancels an existing booking for a user if authorized.
     */
    BookingResponseDTO cancelBooking(Long bookingId, Long userId);

    /**
     * Retrieves all bookings associated with a specific user.
     */
    List<BookingResponseDTO> getBookingsByUser(Long userId);

    /**
     * Checks and returns time-slot availability for an amenity on a given date.
     */
    List<AvailabilityDTO> getAvailability(Long amenityId, LocalDate bookingDate);

    /**
     * Retrieves all bookings belonging to a specific apartment complex.
     */
    List<AdminBookingDTO> getBookingsByApartment(Long apartmentId);

    /**
     * Counts the total active bookings for today within an apartment complex.
     */
    Long getTodayBookingCount(Long apartmentId);
}