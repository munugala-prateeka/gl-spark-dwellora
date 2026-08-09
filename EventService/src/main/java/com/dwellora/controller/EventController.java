package com.dwellora.controller;

import com.dwellora.dto.EventRequestDTO;
import com.dwellora.dto.EventResponseDTO;
import com.dwellora.service.EventService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for managing apartment community events and resident RSVPs. */
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /** Creates a new event for an apartment complex. Restricted to managers. */
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(
            @RequestHeader("X-Apartment-Id") Long apartmentId,
            @Valid @RequestBody EventRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(apartmentId, request));
    }

    /** Retrieves upcoming events for the specified apartment complex. */
    @PreAuthorize("hasAnyRole('RESIDENT', 'MANAGER')")
    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getUpcomingEvents(
            @RequestHeader("X-Apartment-Id") Long apartmentId) {
        return ResponseEntity.ok(eventService.getUpcomingEvents(apartmentId));
    }

    /** Registers an RSVP for an event by a resident. */
    @PreAuthorize("hasRole('RESIDENT')")
    @PostMapping("/{eventId}/rsvp")
    public ResponseEntity<EventResponseDTO> rsvpToEvent(
            @PathVariable Long eventId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Apartment-Id") Long apartmentId) {
        return ResponseEntity.ok(eventService.rsvpToEvent(eventId, userId, apartmentId));
    }

    /** Cancels an existing RSVP for an event by a resident. */
    @PreAuthorize("hasRole('RESIDENT')")
    @DeleteMapping("/{eventId}/rsvp")
    public ResponseEntity<EventResponseDTO> withdrawRsvp(
            @PathVariable Long eventId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Apartment-Id") Long apartmentId) {
        return ResponseEntity.ok(eventService.withdrawRsvp(eventId, userId, apartmentId));
    }

    /** Retrieves event IDs for all active RSVPs belonging to the logged-in resident. */
    @PreAuthorize("hasRole('RESIDENT')")
    @GetMapping("/my-rsvps")
    public ResponseEntity<List<Long>> getMyRsvps(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(eventService.getMyRsvpedEventIds(userId));
    }
}