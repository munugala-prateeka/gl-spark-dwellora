package com.dwellora.dto;

public class LoginResponseDTO {

    private Integer userId;
    private Integer apartmentId;
    private String fullName;
    private String role;
    private String email;
    // add field + getter/setter
    private String token;

    public LoginResponseDTO() {}

    public LoginResponseDTO(
            Integer userId, Integer apartmentId, String fullName, String role, String email) {
        this.userId = userId;
        this.apartmentId = apartmentId;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}