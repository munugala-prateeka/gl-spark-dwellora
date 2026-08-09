package com.dwellora.event;

/**
 * Event published when a complaint status or details are updated.
 */
public class ComplaintUpdatedEvent {

    private Long userId;
    private String category;
    private String status;
    private String resolutionRemark;

    public ComplaintUpdatedEvent() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolutionRemark() {
        return resolutionRemark;
    }

    public void setResolutionRemark(String resolutionRemark) {
        this.resolutionRemark = resolutionRemark;
    }
}