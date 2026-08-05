package com.dwellora.service.impl;

import com.dwellora.dto.EventRequestDTO;
import com.dwellora.dto.EventResponseDTO;
import com.dwellora.entity.Event;
import com.dwellora.event.EventCreatedEvent;
import com.dwellora.repository.EventRepository;
import com.dwellora.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventServiceImpl(EventRepository eventRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.eventRepository = eventRepository;
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

    private EventResponseDTO mapToResponse(Event e) {
        boolean isFull = e.getCapacity() != null && e.getCurrentRsvps() >= e.getCapacity();
        return new EventResponseDTO(
                e.getEventId(),
                e.getApartmentId(),
                e.getTitle(),
                e.getDescription(),
                e.getEventDate(),
                e.getCapacity(),
                e.getCurrentRsvps(), isFull);
    }
}