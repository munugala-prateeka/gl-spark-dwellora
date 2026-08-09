package com.dwellora.dto;

import java.time.LocalTime;

/**
 * Data transfer object containing amenity operational details and booking constraints fetched from external microservices.
 */
public class AmenityDTO {

    private Long amenityId;
    private Long apartmentId;
    private String amenityName;
    private String amenityType;
    private Integer capacity;
    private Boolean available;
    private LocalTime openingTime;
    private LocalTime closingTime;

    private String bookingPolicy;
    private Integer slotDurationMinutes;
    private Integer maxBookingsPerDay;
    private Integer maxBookingsPerMonth;

    public AmenityDTO() {}

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

    public String getAmenityType() {
        return amenityType;
    }

    public void setAmenityType(String amenityType) {
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

    public String getBookingPolicy() {
        return bookingPolicy;
    }

    public void setBookingPolicy(String bookingPolicy) {
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