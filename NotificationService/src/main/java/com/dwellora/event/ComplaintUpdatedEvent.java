package com.dwellora.event;

public class ComplaintUpdatedEvent {
    private Integer userId;
    private String category;
    private String status;
    private String resolutionRemark;

    public ComplaintUpdatedEvent() {}

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResolutionRemark() { return resolutionRemark; }
    public void setResolutionRemark(String resolutionRemark) { this.resolutionRemark = resolutionRemark; }
}