package com.dwellora.dto;
import com.dwellora.enums.BookingStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingResponseDTO {

    private Integer bookingId;

    private Integer userId;

    private Integer amenityId;

    private String amenityName;

    private LocalDate bookingDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private BookingStatus bookingStatus;

    public BookingResponseDTO() {
    }

    public BookingResponseDTO(Integer bookingId,
                              Integer userId,
                              Integer amenityId,
                              String amenityName,
                              LocalDate bookingDate,
                              LocalTime startTime,
                              LocalTime endTime,
                              BookingStatus bookingStatus) {

        this.bookingId = bookingId;
        this.userId = userId;
        this.amenityId = amenityId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(Integer amenityId) {
        this.amenityId = amenityId;
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
