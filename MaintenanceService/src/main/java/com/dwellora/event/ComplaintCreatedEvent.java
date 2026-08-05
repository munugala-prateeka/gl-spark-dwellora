package com.dwellora.event;

public class ComplaintCreatedEvent {
    private Integer userId;
    private String category;
    private String flatNumber;

    public ComplaintCreatedEvent() {}

    public ComplaintCreatedEvent(Integer userId, String category, String flatNumber) {
        this.userId = userId;
        this.category = category;
        this.flatNumber = flatNumber;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
}