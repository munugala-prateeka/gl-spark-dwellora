package com.dwellora.service.impl;

import com.dwellora.dto.EventRequestDTO;
import com.dwellora.dto.EventResponseDTO;
import com.dwellora.entity.Event;
import com.dwellora.entity.Rsvp;
import com.dwellora.event.EventCreatedEvent;
import com.dwellora.exception.EventException;
import com.dwellora.repository.EventRepository;
import com.dwellora.repository.RsvpRepository;
import com.dwellora.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final RsvpRepository rsvpRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventServiceImpl(EventRepository eventRepository, RsvpRepository rsvpRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.eventRepository = eventRepository;
        this.rsvpRepository = rsvpRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public EventResponseDTO createEvent(EventRequestDTO request) {
        Event event = new Event();
        event.setApartmentId(request.getApartmentId());
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setCapacity(request.getCapacity());

        Event saved = eventRepository.save(event);

        try {
            EventCreatedEvent eventPayload = new EventCreatedEvent(
                    saved.getApartmentId(),
                    saved.getTitle(),
                    saved.getDescription(),
                    saved.getEventDate()
            );
            kafkaTemplate.send("event-created", eventPayload);
            logger.info("Published event-created notification for eventId: {}", saved.getEventId());
        } catch (Exception e) {
            logger.error("Failed to publish event notification: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    @Override
    public List<EventResponseDTO> getUpcomingEvents(Integer apartmentId) {
        return eventRepository
                .findByApartmentIdAndEventDateAfterOrderByEventDateAsc(apartmentId, LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public EventResponseDTO rsvpToEvent(Integer eventId, Integer residentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException("Event not found with ID: " + eventId));

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new EventException("Cannot RSVP to a past event");
        }

        // AC-2: Capacity check
        if (event.getCapacity() != null && event.getCurrentRsvps() >= event.getCapacity()) {
            throw new EventException("RSVP rejected: Event is at full capacity");
        }

        if (rsvpRepository.existsByEventIdAndResidentId(eventId, residentId)) {
            throw new EventException("You have already RSVP'd to this event");
        }

        // AC-1: Record RSVP
        Rsvp rsvp = new Rsvp();
        rsvp.setEventId(eventId);
        rsvp.setResidentId(residentId);
        rsvpRepository.save(rsvp);

        event.setCurrentRsvps(event.getCurrentRsvps() + 1);
        Event updated = eventRepository.save(event);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public EventResponseDTO withdrawRsvp(Integer eventId, Integer residentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException("Event not found with ID: " + eventId));

        Rsvp rsvp = rsvpRepository.findByEventIdAndResidentId(eventId, residentId)
                .orElseThrow(() -> new EventException("No RSVP found for this event and resident"));

        // AC-3: Release spot
        rsvpRepository.delete(rsvp);
        event.setCurrentRsvps(Math.max(0, event.getCurrentRsvps() - 1));
        Event updated = eventRepository.save(event);

        return mapToResponse(updated);
    }

    private EventResponseDTO mapToResponse(Event e) {
        boolean isFull = e.getCapacity() != null && e.getCurrentRsvps() >= e.getCapacity();
        return new EventResponseDTO(
                e.getEventId(), e.getApartmentId(), e.getTitle(), e.getDescription(),
                e.getEventDate(), e.getCapacity(), e.getCurrentRsvps(), isFull);
    }
}