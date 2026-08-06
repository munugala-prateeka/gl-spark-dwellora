package com.dwellora.dto;

import com.dwellora.enums.ComplaintStatus;
import java.time.LocalDateTime;

public class ComplaintResponseDTO {

    private Integer complaintId;
    private Integer apartmentId;
    private Integer userId;
    private String flatNumber;
    private String category;
    private String description;
    private ComplaintStatus status;
    private String resolutionRemark;
    private LocalDateTime raisedAt;
    private LocalDateTime resolvedAt;

    public ComplaintResponseDTO() {}

    public ComplaintResponseDTO(Integer complaintId, Integer apartmentId, Integer userId, String flatNumber,
                                String category, String description, ComplaintStatus status,
                                String resolutionRemark, LocalDateTime raisedAt, LocalDateTime resolvedAt) {
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

    public Integer getComplaintId() { return complaintId; }
    public Integer getApartmentId() { return apartmentId; }
    public Integer getUserId() { return userId; }
    public String getFlatNumber() { return flatNumber; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public ComplaintStatus getStatus() { return status; }
    public String getResolutionRemark() { return resolutionRemark; }
    public LocalDateTime getRaisedAt() { return raisedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
}