package com.dwellora.service.impl;

import com.dwellora.dto.EventRequestDTO;
import com.dwellora.dto.EventResponseDTO;
import com.dwellora.entity.Event;
import com.dwellora.entity.Rsvp;
import com.dwellora.event.EventCreatedEvent;
import com.dwellora.event.RsvpCancelledEvent;
import com.dwellora.event.RsvpConfirmedEvent;
import com.dwellora.exception.EventException;
import com.dwellora.repository.EventRepository;
import com.dwellora.repository.RsvpRepository;
import com.dwellora.service.EventService;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of EventService for creating events and managing RSVPs. */
@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final RsvpRepository rsvpRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventServiceImpl(
            EventRepository eventRepository,
            RsvpRepository rsvpRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.eventRepository = eventRepository;
        this.rsvpRepository = rsvpRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public EventResponseDTO createEvent(Long apartmentId, EventRequestDTO request) {

        Event event = new Event();
        event.setApartmentId(apartmentId);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setCapacity(request.getCapacity());
        event.setCurrentRsvps(0);

        Event saved = eventRepository.save(event);

        try {
            EventCreatedEvent eventPayload =
                    new EventCreatedEvent(
                            saved.getApartmentId(),
                            saved.getTitle(),
                            saved.getDescription(),
                            saved.getEventDate());

            kafkaTemplate.send("event-created", eventPayload);

            logger.info(
                    "Published event-created notification for eventId: {}", saved.getEventId());

        } catch (Exception e) {
            logger.error("Failed to publish event notification: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    @Override
    public List<EventResponseDTO> getUpcomingEvents(Long apartmentId) {
        return eventRepository
                .findByApartmentIdAndEventDateAfterOrderByEventDateAsc(
                        apartmentId, LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public EventResponseDTO rsvpToEvent(Long eventId, Long residentId, Long apartmentId) {

        Event event =
                eventRepository
                        .findById(eventId)
                        .orElseThrow(() -> new EventException("Event not found with ID: " + eventId));

        if (!event.getApartmentId().equals(apartmentId)) {
            throw new EventException("You cannot access an event from another apartment");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new EventException("Cannot RSVP to a past event");
        }

        if (event.getCapacity() != null && event.getCurrentRsvps() >= event.getCapacity()) {
            throw new EventException("RSVP rejected: Event is at full capacity");
        }

        if (rsvpRepository.existsByEventIdAndResidentId(eventId, residentId)) {
            throw new EventException("You have already RSVP'd to this event");
        }

        Rsvp rsvp = new Rsvp();
        rsvp.setEventId(eventId);
        rsvp.setResidentId(residentId);

        rsvpRepository.save(rsvp);

        event.setCurrentRsvps(event.getCurrentRsvps() + 1);

        Event updated = eventRepository.save(event);

        try {
            RsvpConfirmedEvent rsvpEvent =
                    new RsvpConfirmedEvent(residentId, event.getTitle(), event.getEventDate());

            kafkaTemplate.send("rsvp-confirmed", rsvpEvent);

            logger.info("Published rsvp-confirmed notification for residentId: {}", residentId);

        } catch (Exception e) {
            logger.error("Failed to publish RSVP confirmation event: {}", e.getMessage());
        }

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EventResponseDTO withdrawRsvp(Long eventId, Long residentId, Long apartmentId) {

        Event event =
                eventRepository
                        .findById(eventId)
                        .orElseThrow(() -> new EventException("Event not found with ID: " + eventId));

        if (!event.getApartmentId().equals(apartmentId)) {
            throw new EventException("You cannot access an event from another apartment");
        }

        Rsvp rsvp =
                rsvpRepository
                        .findByEventIdAndResidentId(eventId, residentId)
                        .orElseThrow(
                                () -> new EventException("No RSVP found for this event and resident"));

        rsvpRepository.delete(rsvp);

        event.setCurrentRsvps(Math.max(0, event.getCurrentRsvps() - 1));

        Event updated = eventRepository.save(event);

        try {
            RsvpCancelledEvent cancelledEvent =
                    new RsvpCancelledEvent(residentId, event.getTitle(), event.getEventDate());

            kafkaTemplate.send("rsvp-cancelled", cancelledEvent);

            logger.info("Published rsvp-cancelled event for residentId: {}", residentId);

        } catch (Exception e) {
            logger.error("Failed to publish RSVP cancellation event: {}", e.getMessage());
        }

        return mapToResponse(updated);
    }

    @Override
    public List<Long> getMyRsvpedEventIds(Long residentId) {
        return rsvpRepository.findByResidentId(residentId).stream()
                .map(Rsvp::getEventId)
                .toList();
    }

    private EventResponseDTO mapToResponse(Event event) {
        boolean isFull =
                event.getCapacity() != null && event.getCurrentRsvps() >= event.getCapacity();

        return new EventResponseDTO(
                event.getEventId(),
                event.getApartmentId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getCapacity(),
                event.getCurrentRsvps(),
                isFull);
    }
}