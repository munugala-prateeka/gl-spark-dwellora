package com.dwellora.dto;

import jakarta.validation.constraints.NotBlank;

/** Data transfer object for raising a new complaint. */
public class ComplaintRequestDTO {

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Description is required")
    private String description;

    public ComplaintRequestDTO() {}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}