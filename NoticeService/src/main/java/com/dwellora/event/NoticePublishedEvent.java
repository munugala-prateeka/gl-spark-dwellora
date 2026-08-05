package com.dwellora.event;

public class NoticePublishedEvent {
    private Integer apartmentId;
    private String title;
    private String body;
    private Boolean isUrgent;

    public NoticePublishedEvent() {}

    public NoticePublishedEvent(Integer apartmentId, String title, String body, Boolean isUrgent) {
        this.apartmentId = apartmentId;
        this.title = title;
        this.body = body;
        this.isUrgent = isUrgent;
    }

    public Integer getApartmentId() { return apartmentId; }
    public void setApartmentId(Integer apartmentId) { this.apartmentId = apartmentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Boolean getIsUrgent() { return isUrgent; }
    public void setIsUrgent(Boolean isUrgent) { this.isUrgent = isUrgent; }
}