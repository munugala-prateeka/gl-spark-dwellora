package com.dwellora.service;

import com.dwellora.dto.AdminBookingDTO;
import com.dwellora.dto.AvailabilityDTO;
import com.dwellora.dto.BookingRequestDTO;
import com.dwellora.dto.BookingResponseDTO;
import com.dwellora.entity.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingResponseDTO addBooking(BookingRequestDTO bookingRequestDTO);

    List<BookingResponseDTO> getAllBookings();

    BookingResponseDTO getBookingById(Integer id);

    List<BookingResponseDTO> getBookingsByDate(LocalDate bookingDate);

    BookingResponseDTO updateBooking(Integer id, BookingRequestDTO bookingRequestDTO);

    List<BookingResponseDTO> getBookingsByUser(Integer userId);

    List<AvailabilityDTO> getAvailability(Integer amenityId, LocalDate bookingDate);

    BookingResponseDTO cancelBooking(Integer bookingId);

}