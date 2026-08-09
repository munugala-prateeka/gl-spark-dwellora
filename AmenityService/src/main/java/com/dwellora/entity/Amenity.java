package com.dwellora.entity;

import com.dwellora.enums.AmenityType;
import com.dwellora.enums.BookingPolicy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;

/** Entity representing an amenity record in the database. */
@Entity
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amenity_id")
    private Long amenityId;

    @Column(name = "apartment_id")
    private Long apartmentId;

    @Column(name = "amenity_name")
    private String amenityName;

    @Enumerated(EnumType.STRING)
    @Column(name = "amenity_type")
    private AmenityType amenityType;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_policy")
    private BookingPolicy bookingPolicy;

    @Column(name = "slot_duration_minutes")
    private Integer slotDurationMinutes;

    @Column(name = "max_bookings_per_day")
    private Integer maxBookingsPerDay;

    @Column(name = "max_bookings_per_month")
    private Integer maxBookingsPerMonth;

    public Amenity() {}

    public Amenity(
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
            Integer maxBookingsPerDay) {
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