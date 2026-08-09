package com.dwellora.dto;

/**
 * Data transfer object representing the capacity and available slots for a specific amenity time window.
 */
public class AvailabilityDTO {

    private String slot;
    private Integer capacity;
    private Long booked;
    private Long remaining;
    private String bookingPolicy;

    public AvailabilityDTO() {}

    public AvailabilityDTO(
            String slot,
            Integer capacity,
            Long booked,
            Long remaining,
            String bookingPolicy) {

        this.slot = slot;
        this.capacity = capacity;
        this.booked = booked;
        this.remaining = remaining;
        this.bookingPolicy = bookingPolicy;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Long getBooked() {
        return booked;
    }

    public void setBooked(Long booked) {
        this.booked = booked;
    }

    public Long getRemaining() {
        return remaining;
    }

    public void setRemaining(Long remaining) {
        this.remaining = remaining;
    }

    public String getBookingPolicy() {
        return bookingPolicy;
    }

    public void setBookingPolicy(String bookingPolicy) {
        this.bookingPolicy = bookingPolicy;
    }
}