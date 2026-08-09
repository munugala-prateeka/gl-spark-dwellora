package com.dwellora.dto;

/**
 * Data transfer object for user login responses.
 */
public class LoginResponseDTO {

    private Long userId;
    private Long apartmentId;
    private String fullName;
    private String role;
    private String email;
    private String token;

    public LoginResponseDTO() {}

    public LoginResponseDTO(
            Long userId, Long apartmentId, String fullName, String role, String email) {
        this.userId = userId;
        this.apartmentId = apartmentId;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
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