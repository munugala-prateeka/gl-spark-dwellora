package com.dwellora.dto;

import com.dwellora.enums.BookingStatus;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data transfer object containing full response details of a booked amenity slot.
 */
public class BookingResponseDTO {

    private Long bookingId;

    private Long userId;

    private Long amenityId;

    private String amenityName;

    private Long apartmentId;

    private LocalDate bookingDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private BookingStatus bookingStatus;

    public BookingResponseDTO() {}

    public BookingResponseDTO(
            Long bookingId,
            Long userId,
            Long amenityId,
            String amenityName,
            Long apartmentId,
            LocalDate bookingDate,
            LocalTime startTime,
            LocalTime endTime,
            BookingStatus bookingStatus) {

        this.bookingId = bookingId;
        this.userId = userId;
        this.amenityId = amenityId;
        this.amenityName = amenityName;
        this.apartmentId = apartmentId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(Long amenityId) {
        this.amenityId = amenityId;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}