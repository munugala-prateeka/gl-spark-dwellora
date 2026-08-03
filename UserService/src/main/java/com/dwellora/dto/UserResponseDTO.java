package com.dwellora.dto;

import com.dwellora.enums.AccountStatus;
import com.dwellora.enums.Role;

public class UserResponseDTO {

    private Integer userId;
    private Integer apartmentId;
    private String fullName;
    private String email;
    private String phone;
    private String flatNumber;
    private Role role;
    private AccountStatus accountStatus;

    public UserResponseDTO() {}

    public UserResponseDTO(
            Integer userId,
            Integer apartmentId,
            String fullName,
            String email,
            String phone,
            String flatNumber,
            Role role,
            AccountStatus accountStatus) {
        this.userId = userId;
        this.apartmentId = apartmentId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.flatNumber = flatNumber;
        this.role = role;
        this.accountStatus = accountStatus;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}