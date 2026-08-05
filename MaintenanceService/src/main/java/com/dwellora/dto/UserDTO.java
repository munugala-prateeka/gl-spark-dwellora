package com.dwellora.dto;

public class UserDTO {
    private Integer userId;
    private Integer apartmentId;
    private String fullName;
    private String flatNumber;
    private String role;

    public UserDTO() {}
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getApartmentId() { return apartmentId; }
    public void setApartmentId(Integer apartmentId) { this.apartmentId = apartmentId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}