package com.dwellora.service;

import com.dwellora.dto.EventRequestDTO;
import com.dwellora.dto.EventResponseDTO;

import java.util.List;

public interface EventService {
    EventResponseDTO createEvent(EventRequestDTO request);
    List<EventResponseDTO> getUpcomingEvents(Integer apartmentId);
    EventResponseDTO rsvpToEvent(Integer eventId, Integer residentId);
    EventResponseDTO withdrawRsvp(Integer eventId, Integer residentId);
    List<Integer> getMyRsvpedEventIds(Integer residentId);
}