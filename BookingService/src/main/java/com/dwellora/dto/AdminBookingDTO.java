package com.dwellora.dto;

import com.dwellora.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class AdminBookingDTO {

    private Integer bookingId;
    private String residentName;
    private String flatNumber;
    private String amenityName;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingStatus bookingStatus;

    public AdminBookingDTO() {
    }

    public AdminBookingDTO(Integer bookingId,
                           String residentName,
                           String flatNumber,
                           String amenityName,
                           LocalDate bookingDate,
                           LocalTime startTime,
                           LocalTime endTime,
                           BookingStatus bookingStatus) {

        this.bookingId = bookingId;
        this.residentName = residentName;
        this.flatNumber = flatNumber;
        this.amenityName = amenityName;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
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