package com.dwellora.repository;

import com.dwellora.entity.Booking;
import com.dwellora.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserId(Integer userId);

    List<Booking> findByAmenityId(Integer amenityId);

    List<Booking> findByBookingDate(LocalDate bookingDate);

    List<Booking> findByAmenityIdAndBookingDate(
            Integer amenityId,
            LocalDate bookingDate);

    long countByAmenityIdAndBookingDateAndStartTimeAndEndTime(
            Integer amenityId,
            LocalDate bookingDate,
            LocalTime startTime,
            LocalTime endTime);

    long countByBookingDate(LocalDate bookingDate);


    long countByUserIdAndBookingDateAndBookingStatus(
            Integer userId,
            LocalDate bookingDate,
            BookingStatus bookingStatus
    );

    List<Booking> findByAmenityIdAndBookingDateAndBookingStatus(
            Integer amenityId,
            LocalDate bookingDate,
            BookingStatus bookingStatus
    );

    long countByUserIdAndAmenityIdAndBookingDateAndBookingStatus(
            Integer userId,
            Integer amenityId,
            LocalDate bookingDate,
            BookingStatus bookingStatus
    );

    long countByUserIdAndAmenityIdAndBookingDateBetweenAndBookingStatus(
            Integer userId,
            Integer amenityId,
            LocalDate startDate,
            LocalDate endDate,
            BookingStatus bookingStatus
    );
}
