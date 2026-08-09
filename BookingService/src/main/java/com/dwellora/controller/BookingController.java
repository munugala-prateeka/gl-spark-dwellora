package com.dwellora.controller;

import com.dwellora.dto.AdminBookingDTO;
import com.dwellora.dto.AvailabilityDTO;
import com.dwellora.dto.BookingRequestDTO;
import com.dwellora.dto.BookingResponseDTO;
import com.dwellora.service.BookingService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing amenity booking operations for residents and managers.
 */
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Creates a new booking for a resident.
     */
    @PreAuthorize("hasRole('RESIDENT')")
    @PostMapping
    public ResponseEntity<BookingResponseDTO> addBooking(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody BookingRequestDTO bookingRequestDTO) {

        BookingResponseDTO response = bookingService.addBooking(userId, bookingRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all amenity bookings for manager overview.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {

        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    /**
     * Retrieves details of a specific booking by its ID.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {

        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    /**
     * Retrieves bookings for a given date.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/date")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByDate(
            @RequestParam LocalDate bookingDate) {

        return ResponseEntity.ok(bookingService.getBookingsByDate(bookingDate));
    }

    /**
     * Retrieves all bookings created by the requesting resident.
     */
    @PreAuthorize("hasRole('RESIDENT')")
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(
            @RequestHeader("X-User-Id") Long userId) {

        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    /**
     * Cancels a resident's existing booking.
     */
    @PreAuthorize("hasRole('RESIDENT')")
    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<BookingResponseDTO> cancelBooking(
            @RequestHeader("X-User-Id") Long userId, @PathVariable Long bookingId) {

        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, userId));
    }

    /**
     * Checks amenity availability for a specific date.
     */
    @PreAuthorize("hasRole('RESIDENT')")
    @GetMapping("/availability/{amenityId}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailability(
            @PathVariable Long amenityId, @RequestParam LocalDate bookingDate) {

        return ResponseEntity.ok(bookingService.getAvailability(amenityId, bookingDate));
    }

    /**
     * Retrieves all bookings for a specific apartment community.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/apartment/{apartmentId}")
    public ResponseEntity<List<AdminBookingDTO>> getBookingsByApartment(
            @PathVariable Long apartmentId) {

        return ResponseEntity.ok(bookingService.getBookingsByApartment(apartmentId));
    }

    /**
     * Retrieves the count of bookings made today for an apartment community.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/apartment/{apartmentId}/today/count")
    public ResponseEntity<Long> getTodayBookingCount(@PathVariable Long apartmentId) {

        return ResponseEntity.ok(bookingService.getTodayBookingCount(apartmentId));
    }
}