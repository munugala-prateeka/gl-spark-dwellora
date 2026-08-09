package com.dwellora.event;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Event payload transfer object published when a new amenity booking is successfully created.
 */
public class BookingCreatedEvent {

    private Long bookingId;

    private Long userId;

    private Long amenityId;

    private String amenityName;

    private LocalDate bookingDate;

    private LocalTime startTime;

    private LocalTime endTime;

    public BookingCreatedEvent() {}

    public BookingCreatedEvent(
            Long bookingId,
            Long userId,
            Long amenityId,
            String amenityName,
            LocalDate bookingDate,
            LocalTime startTime,
            LocalTime endTime) {

        this.bookingId = bookingId;
        this.userId = userId;
        this.amenityId = amenityId;
        this.amenityName = amenityName;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }
}