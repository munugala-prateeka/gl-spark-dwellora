package com.dwellora.dto;

import com.dwellora.enums.ComplaintStatus;
import java.time.LocalDateTime;

/** Data transfer object containing complaint details in response payloads. */
public class ComplaintResponseDTO {

    private Long complaintId;
    private Long apartmentId;
    private Long userId;
    private String flatNumber;
    private String category;
    private String description;
    private ComplaintStatus status;
    private String resolutionRemark;
    private LocalDateTime raisedAt;
    private LocalDateTime resolvedAt;

    public ComplaintResponseDTO() {}

    public ComplaintResponseDTO(
            Long complaintId,
            Long apartmentId,
            Long userId,
            String flatNumber,
            String category,
            String description,
            ComplaintStatus status,
            String resolutionRemark,
            LocalDateTime raisedAt,
            LocalDateTime resolvedAt) {
        this.complaintId = complaintId;
        this.apartmentId = apartmentId;
        this.userId = userId;
        this.flatNumber = flatNumber;
        this.category = category;
        this.description = description;
        this.status = status;
        this.resolutionRemark = resolutionRemark;
        this.raisedAt = raisedAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public String getResolutionRemark() {
        return resolutionRemark;
    }

    public LocalDateTime getRaisedAt() {
        return raisedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
}