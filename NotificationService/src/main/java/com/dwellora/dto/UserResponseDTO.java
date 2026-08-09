package com.dwellora.dto;

/**
 * Data transfer object for user response details.
 */
public class UserResponseDTO {

    private Long userId;
    private Long apartmentId;
    private String fullName;
    private String email;
    private String phone;
    private String flatNumber;
    private String role;
    private String accountStatus;

    public UserResponseDTO() {}

    public UserResponseDTO(
            Long userId,
            Long apartmentId,
            String fullName,
            String email,
            String phone,
            String flatNumber,
            String role,
            String accountStatus) {
        this.userId = userId;
        this.apartmentId = apartmentId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.flatNumber = flatNumber;
        this.role = role;
        this.accountStatus = accountStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}