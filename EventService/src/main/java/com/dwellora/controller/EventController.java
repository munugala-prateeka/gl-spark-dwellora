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
}