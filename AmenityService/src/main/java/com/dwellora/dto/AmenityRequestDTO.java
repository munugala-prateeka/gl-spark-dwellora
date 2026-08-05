package com.dwellora.dto;

import com.dwellora.enums.AmenityType;
import com.dwellora.enums.BookingPolicy;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class AmenityRequestDTO {

    @NotNull(message = "Apartment ID is required")
    private Integer apartmentId;

    @NotBlank(message = "Amenity name is required")
    private String amenityName;

    @NotNull(message = "Amenity type is required")
    private AmenityType amenityType;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private Boolean available = true;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;

    @NotNull(message = "Booking policy is required")
    private BookingPolicy bookingPolicy;

    @NotNull(message = "Slot duration is required")
    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    private Integer slotDurationMinutes;

    @NotNull(message = "Max bookings per day is required")
    @Min(value = 1, message = "Max bookings per day must be at least 1")
    private Integer maxBookingsPerDay;

    private Integer maxBookingsPerMonth;

    public AmenityRequestDTO() {}

    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
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