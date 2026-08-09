package com.dwellora.entity;

import com.dwellora.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing an apartment.
 */
@Entity
@Table(name = "apartments")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apartment_id")
    private Long apartmentId;

    @Column(name = "apartment_name")
    private String apartmentName;

    private String address;
    private String city;
    private String state;
    private String pincode;

    @Column(name = "total_blocks")
    private Integer totalBlocks;

    @Column(name = "total_units")
    private Integer totalUnits;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Apartment() {}

    public Apartment(
            Long apartmentId,
            String apartmentName,
            String address,
            String city,
            String state,
            String pincode,
            Integer totalBlocks,
            Integer totalUnits,
            Status status) {
        this.apartmentId = apartmentId;
        this.apartmentName = apartmentName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.totalBlocks = totalBlocks;
        this.totalUnits = totalUnits;
        this.status = status;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Integer getTotalBlocks() {
        return totalBlocks;
    }

    public void setTotalBlocks(Integer totalBlocks) {
        this.totalBlocks = totalBlocks;
    }

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}