package com.dwellora.event;

import java.time.LocalDateTime;

/**
 * Event published when a community event is created.
 */
public class EventCreatedEvent {

    private Long apartmentId;
    private String title;
    private String description;
    private LocalDateTime eventDate;

    public EventCreatedEvent() {}

    public EventCreatedEvent(
            Long apartmentId, String title, String description, LocalDateTime eventDate) {
        this.apartmentId = apartmentId;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}