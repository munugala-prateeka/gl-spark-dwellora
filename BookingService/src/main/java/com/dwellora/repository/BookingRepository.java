package com.dwellora.repository;

import com.dwellora.entity.Booking;
import com.dwellora.enums.BookingStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Booking} persistence operations and custom queries.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByAmenityId(Long amenityId);

    List<Booking> findByBookingDate(LocalDate bookingDate);

    List<Booking> findByAmenityIdAndBookingDate(Long amenityId, LocalDate bookingDate);

    long countByAmenityIdAndBookingDateAndStartTimeAndEndTime(
            Long amenityId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime);

    long countByBookingDate(LocalDate bookingDate);

    long countByUserIdAndBookingDateAndBookingStatus(
            Long userId, LocalDate bookingDate, BookingStatus bookingStatus);

    List<Booking> findByAmenityIdAndBookingDateAndBookingStatus(
            Long amenityId, LocalDate bookingDate, BookingStatus bookingStatus);

    long countByUserIdAndAmenityIdAndBookingDateAndBookingStatus(
            Long userId, Long amenityId, LocalDate bookingDate, BookingStatus bookingStatus);

    long countByUserIdAndAmenityIdAndBookingDateBetweenAndBookingStatus(
            Long userId,
            Long amenityId,
            LocalDate startDate,
            LocalDate endDate,
            BookingStatus bookingStatus);

    List<Booking> findByApartmentId(Long apartmentId);

    long countByApartmentIdAndBookingDateAndBookingStatus(
            Long apartmentId, LocalDate bookingDate, BookingStatus bookingStatus);
}