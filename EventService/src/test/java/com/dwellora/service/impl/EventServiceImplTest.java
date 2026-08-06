package com.dwellora.service.impl;

import com.dwellora.dto.EventRequestDTO;
import com.dwellora.dto.EventResponseDTO;
import com.dwellora.entity.Event;
import com.dwellora.entity.Rsvp;
import com.dwellora.exception.EventException;
import com.dwellora.repository.EventRepository;
import com.dwellora.repository.RsvpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EventServiceImpl}.
 *
 * <p>Covers US-016 - Manager Creates a Community Event, and US-017 - Resident RSVPs to an Event.
 */
@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RsvpRepository rsvpRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event upcomingEvent;

    @BeforeEach
    void setUp() {
        upcomingEvent = new Event();
        upcomingEvent.setEventId(1);
        upcomingEvent.setApartmentId(10);
        upcomingEvent.setTitle("Diwali Mela");
        upcomingEvent.setDescription("Community Diwali celebration");
        upcomingEvent.setEventDate(LocalDateTime.now().plusDays(7));
        upcomingEvent.setCapacity(2);
        upcomingEvent.setCurrentRsvps(0);
    }

    // ==========================================
    // US-016: CREATE A COMMUNITY EVENT
    // ==========================================

    @Test
    @DisplayName("US-016 (AC-1): Given valid event details, when created, then it appears in the apartment's upcoming events")
    void createEvent_ValidRequest_SavesAndPublishesEvent() {
        // Given
        EventRequestDTO request = new EventRequestDTO();
        request.setApartmentId(10);
        request.setTitle("Diwali Mela");
        request.setDescription("Community Diwali celebration");
        request.setEventDate(LocalDateTime.now().plusDays(7));
        request.setCapacity(2);

        when(eventRepository.save(any(Event.class))).thenReturn(upcomingEvent);

        // When
        EventResponseDTO response = eventService.createEvent(request);

        // Then
        assertNotNull(response);
        assertEquals("Diwali Mela", response.title());
        assertFalse(response.isFull());
        verify(kafkaTemplate, times(1)).send(eq("event-created"), any());
    }

    @Test
    @DisplayName("Given an apartment id, when upcoming events are requested, then only future events for that apartment are returned")
    void getUpcomingEvents_ReturnsFutureEventsForApartment() {
        // Given
        when(eventRepository.findByApartmentIdAndEventDateAfterOrderByEventDateAsc(eq(10), any(LocalDateTime.class)))
                .thenReturn(List.of(upcomingEvent));

        // When
        List<EventResponseDTO> results = eventService.getUpcomingEvents(10);

        // Then
        assertEquals(1, results.size());
        assertEquals("Diwali Mela", results.get(0).title());
    }

    // ==========================================
    // US-017: RSVP TO AN EVENT
    // ==========================================

    @Test
    @DisplayName("US-017 (AC-1): Given an event with remaining capacity, when I RSVP, then my response is recorded against my user ID")
    void rsvpToEvent_WithinCapacity_RecordsRsvp() {
        // Given
        when(eventRepository.findById(1)).thenReturn(Optional.of(upcomingEvent));
        when(rsvpRepository.existsByEventIdAndResidentId(1, 500)).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        // When
        EventResponseDTO response = eventService.rsvpToEvent(1, 500);

        // Then
        assertEquals(1, response.currentRsvps());
        assertFalse(response.isFull());
        verify(rsvpRepository, times(1)).save(any(Rsvp.class));
        verify(kafkaTemplate, times(1)).send(eq("rsvp-confirmed"), any());
    }

    @Test
    @DisplayName("US-017 (AC-2): Given an event at full capacity, when I try to RSVP, then the request is rejected")
    void rsvpToEvent_AtCapacity_ThrowsException() {
        // Given
        upcomingEvent.setCurrentRsvps(2); // capacity is 2
        when(eventRepository.findById(1)).thenReturn(Optional.of(upcomingEvent));

        // When & Then
        EventException ex = assertThrows(EventException.class, () -> eventService.rsvpToEvent(1, 500));
        assertTrue(ex.getMessage().contains("full capacity"));
        verify(rsvpRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-017: Given a resident who already RSVP'd, when they RSVP again, then the request is rejected")
    void rsvpToEvent_AlreadyRsvpd_ThrowsException() {
        // Given
        when(eventRepository.findById(1)).thenReturn(Optional.of(upcomingEvent));
        when(rsvpRepository.existsByEventIdAndResidentId(1, 500)).thenReturn(true);

        // When & Then
        assertThrows(EventException.class, () -> eventService.rsvpToEvent(1, 500));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-017: Given a past event, when RSVP is attempted, then it is rejected")
    void rsvpToEvent_PastEvent_ThrowsException() {
        // Given
        upcomingEvent.setEventDate(LocalDateTime.now().minusDays(1));
        when(eventRepository.findById(1)).thenReturn(Optional.of(upcomingEvent));

        // When & Then
        assertThrows(EventException.class, () -> eventService.rsvpToEvent(1, 500));
    }

    @Test
    @DisplayName("US-017 (AC-3): Given an event I RSVP'd to, when I withdraw, then my spot is released")
    void withdrawRsvp_ExistingRsvp_ReleasesSpot() {
        // Given
        upcomingEvent.setCurrentRsvps(1);
        Rsvp rsvp = new Rsvp();
        rsvp.setRsvpId(9);
        rsvp.setEventId(1);
        rsvp.setResidentId(500);

        when(eventRepository.findById(1)).thenReturn(Optional.of(upcomingEvent));
        when(rsvpRepository.findByEventIdAndResidentId(1, 500)).thenReturn(Optional.of(rsvp));
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        // When
        EventResponseDTO response = eventService.withdrawRsvp(1, 500);

        // Then
        assertEquals(0, response.currentRsvps());
        verify(rsvpRepository, times(1)).delete(rsvp);
        verify(kafkaTemplate, times(1)).send(eq("rsvp-cancelled"), any());
    }

    @Test
    @DisplayName("US-017: Given no RSVP exists for this event and resident, when withdrawal is attempted, then it is rejected")
    void withdrawRsvp_NoExistingRsvp_ThrowsException() {
        // Given
        when(eventRepository.findById(1)).thenReturn(Optional.of(upcomingEvent));
        when(rsvpRepository.findByEventIdAndResidentId(1, 500)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EventException.class, () -> eventService.withdrawRsvp(1, 500));
        verify(eventRepository, never()).save(any());
    }
}