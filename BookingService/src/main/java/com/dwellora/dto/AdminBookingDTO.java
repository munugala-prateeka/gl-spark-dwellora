package com.dwellora.dto;

import com.dwellora.enums.BookingStatus;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data transfer object representing booking details populated with resident and amenity information for administrative view.
 */
public class AdminBookingDTO {

    private Long bookingId;
    private String residentName;
    private String flatNumber;
    private String amenityName;
    private Long apartmentId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingStatus bookingStatus;

    public AdminBookingDTO() {}

    public AdminBookingDTO(
            Long bookingId,
            String residentName,
            String flatNumber,
            String amenityName,
            Long apartmentId,
            LocalDate bookingDate,
            LocalTime startTime,
            LocalTime endTime,
            BookingStatus bookingStatus) {

        this.bookingId = bookingId;
        this.residentName = residentName;
        this.flatNumber = flatNumber;
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

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
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