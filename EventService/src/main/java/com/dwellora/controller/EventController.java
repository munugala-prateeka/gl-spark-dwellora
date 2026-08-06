package com.dwellora.controller;

import com.dwellora.dto.EventRequestDTO;
import com.dwellora.dto.EventResponseDTO;
import com.dwellora.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO request) {
        return new ResponseEntity<>(eventService.createEvent(request), HttpStatus.CREATED);
    }

    @GetMapping("/apartment/{apartmentId}")
    public ResponseEntity<List<EventResponseDTO>> getUpcomingEvents(@PathVariable Integer apartmentId) {
        return ResponseEntity.ok(eventService.getUpcomingEvents(apartmentId));
    }

    // US-017: RSVP to event
    @PostMapping("/{eventId}/rsvp")
    public ResponseEntity<EventResponseDTO> rsvpToEvent(
            @PathVariable Integer eventId,
            @RequestParam Integer residentId) {
        return ResponseEntity.ok(eventService.rsvpToEvent(eventId, residentId));
    }

    // US-017: Withdraw RSVP from event
    @DeleteMapping("/{eventId}/rsvp")
    public ResponseEntity<EventResponseDTO> withdrawRsvp(
            @PathVariable Integer eventId,
            @RequestParam Integer residentId) {
        return ResponseEntity.ok(eventService.withdrawRsvp(eventId, residentId));
    }

    @GetMapping("/rsvps/{residentId}")
    public ResponseEntity<List<Integer>> getMyRsvps(@PathVariable Integer residentId) {
        return ResponseEntity.ok(eventService.getMyRsvpedEventIds(residentId));
    }
}