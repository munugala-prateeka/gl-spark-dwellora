package com.dwellora.service;

import com.dwellora.dto.EventRequestDTO;
import com.dwellora.dto.EventResponseDTO;

import java.util.List;

public interface EventService {
    EventResponseDTO createEvent(EventRequestDTO request);
    List<EventResponseDTO> getUpcomingEvents(Integer apartmentId);
}