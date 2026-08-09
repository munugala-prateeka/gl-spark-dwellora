package com.dwellora.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/** Entity representing a resident's RSVP registration for a specific event. */
@Entity
@Table(
        name = "rsvps",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"event_id", "resident_id"})})
public class Rsvp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rsvpId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "resident_id", nullable = false)
    private Long residentId;

    @Column(nullable = false)
    private LocalDateTime rsvpDate = LocalDateTime.now();

    public Rsvp() {}

    public Long getRsvpId() {
        return rsvpId;
    }

    public void setRsvpId(Long rsvpId) {
        this.rsvpId = rsvpId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public LocalDateTime getRsvpDate() {
        return rsvpDate;
    }

    public void setRsvpDate(LocalDateTime rsvpDate) {
        this.rsvpDate = rsvpDate;
    }
}