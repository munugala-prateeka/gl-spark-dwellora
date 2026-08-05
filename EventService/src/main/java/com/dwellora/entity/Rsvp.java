package com.dwellora.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rsvps", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "resident_id"})
})
public class Rsvp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rsvpId;

    @Column(name = "event_id", nullable = false)
    private Integer eventId;

    @Column(name = "resident_id", nullable = false)
    private Integer residentId;

    @Column(nullable = false)
    private LocalDateTime rsvpDate = LocalDateTime.now();

    public Integer getRsvpId() { return rsvpId; }
    public void setRsvpId(Integer rsvpId) { this.rsvpId = rsvpId; }

    public Integer getEventId() { return eventId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }

    public Integer getResidentId() { return residentId; }
    public void setResidentId(Integer residentId) { this.residentId = residentId; }

    public LocalDateTime getRsvpDate() { return rsvpDate; }
    public void setRsvpDate(LocalDateTime rsvpDate) { this.rsvpDate = rsvpDate; }
}