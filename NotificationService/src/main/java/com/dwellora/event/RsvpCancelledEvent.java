package com.dwellora.event;

import java.time.LocalDateTime;

public class RsvpCancelledEvent {
    private Integer residentId;
    private String eventTitle;
    private LocalDateTime eventDate;

    public RsvpCancelledEvent() {}

    public RsvpCancelledEvent(Integer residentId, String eventTitle, LocalDateTime eventDate) {
        this.residentId = residentId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
    }

    public Integer getResidentId() { return residentId; }
    public void setResidentId(Integer residentId) { this.residentId = residentId; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
}