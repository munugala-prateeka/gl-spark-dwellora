package com.dwellora.event;

import java.time.LocalDateTime;

/** Event published to Kafka when a resident cancels their RSVP. */
public class RsvpCancelledEvent {

    private Long residentId;
    private String eventTitle;
    private LocalDateTime eventDate;

    public RsvpCancelledEvent() {}

    public RsvpCancelledEvent(Long residentId, String eventTitle, LocalDateTime eventDate) {
        this.residentId = residentId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}