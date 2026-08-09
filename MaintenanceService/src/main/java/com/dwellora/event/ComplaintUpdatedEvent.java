package com.dwellora.event;

/** Event payload published to Kafka when a complaint status or resolution updates. */
public class ComplaintUpdatedEvent {

    private Long userId;
    private String category;
    private String status;
    private String resolutionRemark;

    public ComplaintUpdatedEvent() {}

    public ComplaintUpdatedEvent(
            Long userId, String category, String status, String resolutionRemark) {
        this.userId = userId;
        this.category = category;
        this.status = status;
        this.resolutionRemark = resolutionRemark;
    }

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