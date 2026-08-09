package com.dwellora.dto;

import com.dwellora.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;

/** Data transfer object for updating complaint status and resolution details. */
public class ComplaintUpdateDTO {

    @NotNull(message = "Status is required")
    private ComplaintStatus status;

    private String resolutionRemark;

    public ComplaintUpdateDTO() {}

    public ComplaintUpdateDTO(ComplaintStatus status, String resolutionRemark) {
        this.status = status;
        this.resolutionRemark = resolutionRemark;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getResolutionRemark() {
        return resolutionRemark;
    }

    public void setResolutionRemark(String resolutionRemark) {
        this.resolutionRemark = resolutionRemark;
    }
}