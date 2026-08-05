package com.dwellora.controller;

import com.dwellora.dto.AdminBookingDTO;
import com.dwellora.dto.AvailabilityDTO;
import com.dwellora.dto.BookingRequestDTO;
import com.dwellora.dto.BookingResponseDTO;
import com.dwellora.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDTO> addBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingResponseDTO response = bookingService.addBooking(bookingRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/date")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByDate(@RequestParam LocalDate bookingDate) {
        return ResponseEntity.ok(bookingService.getBookingsByDate(bookingDate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> updateBooking(
            @PathVariable Integer id,
            @Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.ok(bookingService.updateBooking(id, bookingRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Integer id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Integer bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    @GetMapping("/availability/{amenityId}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailability(
            @PathVariable Integer amenityId,
            @RequestParam LocalDate bookingDate) {
        return ResponseEntity.ok(bookingService.getAvailability(amenityId, bookingDate));
    }

    @GetMapping("/apartment/{apartmentId}")
    public ResponseEntity<List<AdminBookingDTO>> getBookingsByApartment(@PathVariable Integer apartmentId) {
        return ResponseEntity.ok(bookingService.getBookingsByApartment(apartmentId));
    }

    @GetMapping("/apartment/{apartmentId}/today/count")
    public ResponseEntity<Long> getTodayBookingCount(@PathVariable Integer apartmentId) {
        return ResponseEntity.ok(bookingService.getTodayBookingCount(apartmentId));
    }
}