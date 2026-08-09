package com.dwellora.dto;

import com.dwellora.enums.AmenityType;
import com.dwellora.enums.BookingPolicy;
import java.time.LocalTime;

/** Data transfer object representing amenity response details. */
public class AmenityResponseDTO {

    private Long amenityId;
    private Long apartmentId;
    private String amenityName;
    private AmenityType amenityType;
    private Integer capacity;
    private Boolean available;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private BookingPolicy bookingPolicy;
    private Integer slotDurationMinutes;
    private Integer maxBookingsPerDay;
    private Integer maxBookingsPerMonth;

    public AmenityResponseDTO() {}

    public AmenityResponseDTO(
            Long amenityId,
            Long apartmentId,
            String amenityName,
            AmenityType amenityType,
            Integer capacity,
            Boolean available,
            LocalTime openingTime,
            LocalTime closingTime,
            BookingPolicy bookingPolicy,
            Integer slotDurationMinutes,
            Integer maxBookingsPerDay,
            Integer maxBookingsPerMonth) {
        this.amenityId = amenityId;
        this.apartmentId = apartmentId;
        this.amenityName = amenityName;
        this.amenityType = amenityType;
        this.capacity = capacity;
        this.available = available;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.bookingPolicy = bookingPolicy;
        this.slotDurationMinutes = slotDurationMinutes;
        this.maxBookingsPerDay = maxBookingsPerDay;
        this.maxBookingsPerMonth = maxBookingsPerMonth;
    }

    public Long getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(Long amenityId) {
        this.amenityId = amenityId;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }

    public AmenityType getAmenityType() {
        return amenityType;
    }

    public void setAmenityType(AmenityType amenityType) {
        this.amenityType = amenityType;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public BookingPolicy getBookingPolicy() {
        return bookingPolicy;
    }

    public void setBookingPolicy(BookingPolicy bookingPolicy) {
        this.bookingPolicy = bookingPolicy;
    }

    public Integer getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public void setSlotDurationMinutes(Integer slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public Integer getMaxBookingsPerDay() {
        return maxBookingsPerDay;
    }

    public void setMaxBookingsPerDay(Integer maxBookingsPerDay) {
        this.maxBookingsPerDay = maxBookingsPerDay;
    }

    public Integer getMaxBookingsPerMonth() {
        return maxBookingsPerMonth;
    }

    public void setMaxBookingsPerMonth(Integer maxBookingsPerMonth) {
        this.maxBookingsPerMonth = maxBookingsPerMonth;
    }
}