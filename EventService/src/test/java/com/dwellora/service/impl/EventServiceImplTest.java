package com.dwellora.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dwellora.dto.EventRequestDTO;
import com.dwellora.dto.EventResponseDTO;
import com.dwellora.entity.Event;
import com.dwellora.entity.Rsvp;
import com.dwellora.exception.EventException;
import com.dwellora.repository.EventRepository;
import com.dwellora.repository.RsvpRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Unit tests for {@link EventServiceImpl}.
 *
 * <p>Covers US-016 - Manager Creates a Community Event, and US-017 - Resident RSVPs to an Event.
 */
@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock private EventRepository eventRepository;

    @Mock private RsvpRepository rsvpRepository;

    @Mock private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks private EventServiceImpl eventService;

    private Event upcomingEvent;

    /** Sets up sample event data before each test execution. */
    @BeforeEach
    void setUp() {
        upcomingEvent = new Event();
        upcomingEvent.setEventId(1L);
        upcomingEvent.setApartmentId(10L);
        upcomingEvent.setTitle("Diwali Mela");
        upcomingEvent.setDescription("Community Diwali celebration");
        upcomingEvent.setEventDate(LocalDateTime.now().plusDays(7));
        upcomingEvent.setCapacity(2);
        upcomingEvent.setCurrentRsvps(0);
    }

    // ==========================================
    // US-016: CREATE A COMMUNITY EVENT
    // ==========================================

    /** Tests event creation and Kafka event publication for valid request data. */
    @Test
    @DisplayName(
            "US-016 (AC-1): Given valid event details, when created, then it appears in the apartment's"
                    + " upcoming events")
    void createEvent_ValidRequest_SavesAndPublishesEvent() {
        EventRequestDTO request = new EventRequestDTO();
        request.setTitle("Diwali Mela");
        request.setDescription("Community Diwali celebration");
        request.setEventDate(LocalDateTime.now().plusDays(7));
        request.setCapacity(2);

        when(eventRepository.save(any(Event.class))).thenReturn(upcomingEvent);

        EventResponseDTO response = eventService.createEvent(10L, request);

        assertNotNull(response);
        assertEquals("Diwali Mela", response.title());
        assertFalse(response.isFull());
        verify(kafkaTemplate, times(1)).send(eq("event-created"), any());
    }

    /** Tests retrieving upcoming events scoped to a specific apartment complex. */
    @Test
    @DisplayName(
            "Given an apartment id, when upcoming events are requested, then only future events for"
                    + " that apartment are returned")
    void getUpcomingEvents_ReturnsFutureEventsForApartment() {
        when(eventRepository.findByApartmentIdAndEventDateAfterOrderByEventDateAsc(
                eq(10L), any(LocalDateTime.class)))
                .thenReturn(List.of(upcomingEvent));

        List<EventResponseDTO> results = eventService.getUpcomingEvents(10L);

        assertEquals(1, results.size());
        assertEquals("Diwali Mela", results.get(0).title());
    }

    // ==========================================
    // US-017: RSVP TO AN EVENT
    // ==========================================

    /** Tests registering an RSVP for a resident when capacity is available. */
    @Test
    @DisplayName(
            "US-017 (AC-1): Given an event with remaining capacity, when I RSVP, then my response is"
                    + " recorded against my user ID")
    void rsvpToEvent_WithinCapacity_RecordsRsvp() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));
        when(rsvpRepository.existsByEventIdAndResidentId(1L, 500L)).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        EventResponseDTO response = eventService.rsvpToEvent(1L, 500L, 10L);

        assertEquals(1, response.currentRsvps());
        assertFalse(response.isFull());
        verify(rsvpRepository, times(1)).save(any(Rsvp.class));
        verify(kafkaTemplate, times(1)).send(eq("rsvp-confirmed"), any());
    }

    /** Tests RSVP rejection when the event has reached its maximum capacity. */
    @Test
    @DisplayName(
            "US-017 (AC-2): Given an event at full capacity, when I try to RSVP, then the request is"
                    + " rejected")
    void rsvpToEvent_AtCapacity_ThrowsException() {
        upcomingEvent.setCurrentRsvps(2); // capacity is 2

        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));

        EventException ex =
                assertThrows(EventException.class, () -> eventService.rsvpToEvent(1L, 500L, 10L));
        assertEquals("RSVP rejected: Event is at full capacity", ex.getMessage());
        verify(rsvpRepository, never()).save(any());
    }

    /** Tests RSVP rejection when a resident attempts to RSVP to an event twice. */
    @Test
    @DisplayName(
            "US-017: Given a resident who already RSVP'd, when they RSVP again, then the request is"
                    + " rejected")
    void rsvpToEvent_AlreadyRsvpd_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));
        when(rsvpRepository.existsByEventIdAndResidentId(1L, 500L)).thenReturn(true);

        EventException ex =
                assertThrows(EventException.class, () -> eventService.rsvpToEvent(1L, 500L, 10L));
        assertEquals("You have already RSVP'd to this event", ex.getMessage());
        verify(eventRepository, never()).save(any());
    }

    /** Tests RSVP rejection when attempting to register for an event in the past. */
    @Test
    @DisplayName("US-017: Given a past event, when RSVP is attempted, then it is rejected")
    void rsvpToEvent_PastEvent_ThrowsException() {
        upcomingEvent.setEventDate(LocalDateTime.now().minusDays(1));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));

        EventException ex =
                assertThrows(EventException.class, () -> eventService.rsvpToEvent(1L, 500L, 10L));
        assertEquals("Cannot RSVP to a past event", ex.getMessage());
    }

    /** Tests RSVP rejection when attempting to access an event belonging to another apartment. */
    @Test
    @DisplayName(
            "US-017: Given an event belonging to another apartment, when RSVP is attempted, then it is"
                    + " rejected")
    void rsvpToEvent_MismatchedApartment_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));

        EventException ex =
                assertThrows(EventException.class, () -> eventService.rsvpToEvent(1L, 500L, 99L));
        assertEquals("You cannot access an event from another apartment", ex.getMessage());
    }

    /** Tests withdrawing an RSVP and verifying spot release and event emission. */
    @Test
    @DisplayName(
            "US-017 (AC-3): Given an event I RSVP'd to, when I withdraw, then my spot is released")
    void withdrawRsvp_ExistingRsvp_ReleasesSpot() {
        upcomingEvent.setCurrentRsvps(1);
        Rsvp rsvp = new Rsvp();
        rsvp.setRsvpId(9L);
        rsvp.setEventId(1L);
        rsvp.setResidentId(500L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));
        when(rsvpRepository.findByEventIdAndResidentId(1L, 500L)).thenReturn(Optional.of(rsvp));
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        EventResponseDTO response = eventService.withdrawRsvp(1L, 500L, 10L);

        assertEquals(0, response.currentRsvps());
        verify(rsvpRepository, times(1)).delete(rsvp);
        verify(kafkaTemplate, times(1)).send(eq("rsvp-cancelled"), any());
    }

    /** Tests rejection when attempting to withdraw an RSVP that does not exist. */
    @Test
    @DisplayName(
            "US-017: Given no RSVP exists for this event and resident, when withdrawal is attempted,"
                    + " then it is rejected")
    void withdrawRsvp_NoExistingRsvp_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));
        when(rsvpRepository.findByEventIdAndResidentId(1L, 500L)).thenReturn(Optional.empty());

        EventException ex =
                assertThrows(EventException.class, () -> eventService.withdrawRsvp(1L, 500L, 10L));
        assertEquals("No RSVP found for this event and resident", ex.getMessage());
        verify(eventRepository, never()).save(any());
    }
}