package com.dwellora.service;

import com.dwellora.dto.EventRequestDTO;
import com.dwellora.dto.EventResponseDTO;
import java.util.List;

/** Service interface defining operations for managing events and resident RSVPs. */
public interface EventService {

    /** Creates a new event for an apartment complex. */
    EventResponseDTO createEvent(Long apartmentId, EventRequestDTO request);

    /** Retrieves all upcoming events for an apartment complex. */
    List<EventResponseDTO> getUpcomingEvents(Long apartmentId);

    /** Registers an RSVP for an event by a resident. */
    EventResponseDTO rsvpToEvent(Long eventId, Long residentId, Long apartmentId);

    /** Withdraws an RSVP for an event by a resident. */
    EventResponseDTO withdrawRsvp(Long eventId, Long residentId, Long apartmentId);

    /** Retrieves event IDs for all active RSVPs belonging to a resident. */
    List<Long> getMyRsvpedEventIds(Long residentId);
}