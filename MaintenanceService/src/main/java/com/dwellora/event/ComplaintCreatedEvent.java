package com.dwellora.event;

/** Event payload published to Kafka when a new complaint is created. */
public class ComplaintCreatedEvent {

    private Long userId;
    private String category;
    private String flatNumber;

    public ComplaintCreatedEvent() {}

    public ComplaintCreatedEvent(Long userId, String category, String flatNumber) {
        this.userId = userId;
        this.category = category;
        this.flatNumber = flatNumber;
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

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }
}