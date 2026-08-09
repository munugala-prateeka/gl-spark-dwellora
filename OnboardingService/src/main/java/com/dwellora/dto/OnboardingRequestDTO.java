package com.dwellora.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data transfer object for creating an onboarding request.
 */
public class OnboardingRequestDTO {

    @NotBlank(message = "Apartment name is required")
    private String apartmentName;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must contain 6 digits")
    private String pincode;

    @Min(value = 1, message = "Blocks must be greater than 0")
    private Integer totalBlocks;

    @Min(value = 1, message = "Units must be greater than 0")
    private Integer totalUnits;

    @NotBlank(message = "Manager name is required")
    private String managerName;

    @Email(message = "Invalid email")
    private String managerEmail;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must contain 10 digits")
    private String managerPhone;

    public OnboardingRequestDTO() {}

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

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }
}