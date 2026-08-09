package com.dwellora.event;

/** Event payload published to Kafka when a new notice is published. */
public class NoticePublishedEvent {

    private Long apartmentId;
    private String title;
    private String body;
    private Boolean isUrgent;

    public NoticePublishedEvent() {}

    public NoticePublishedEvent(Long apartmentId, String title, String body, Boolean isUrgent) {
        this.apartmentId = apartmentId;
        this.title = title;
        this.body = body;
        this.isUrgent = isUrgent;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }
}