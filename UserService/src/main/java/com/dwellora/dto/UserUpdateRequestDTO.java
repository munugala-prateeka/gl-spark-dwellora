package com.dwellora.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data transfer object for user update requests.
 */
public class UserUpdateRequestDTO {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^$|^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&#]).{8,}$",
            message =
                    "Password must be at least 8 characters and include a letter, a number, and a special"
                            + " character (@$!%*?&#), if provided")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String flatNumber;

    public UserUpdateRequestDTO() {}

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}