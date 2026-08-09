package com.dwellora.kafka;

import com.dwellora.client.UserClient;
import com.dwellora.dto.UserResponseDTO;
import com.dwellora.entity.Notification;
import com.dwellora.event.EventCreatedEvent;
import com.dwellora.event.RsvpCancelledEvent;
import com.dwellora.event.RsvpConfirmedEvent;
import com.dwellora.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private final UserClient userClient;
    private final NotificationRepository notificationRepository;

    public EventConsumer(UserClient userClient, NotificationRepository notificationRepository) {
        this.userClient = userClient;
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(
            topics = "event-created",
            groupId = "notification-event-group",
            containerFactory = "eventKafkaListenerContainerFactory")
    public void consumeEventCreated(EventCreatedEvent event) {
        logger.info(
                "Received event-created event for apartmentId: {}, Title: {}",
                event.getApartmentId(),
                event.getTitle());

        try {
            List<UserResponseDTO> residents = userClient.getResidentsByApartment(event.getApartmentId());

            if (residents.isEmpty()) {
                logger.info("No residents found for apartment ID: {}", event.getApartmentId());
                return;
            }

            List<Notification> notifications =
                    residents.stream()
                            .map(
                                    resident -> {
                                        Notification notification = new Notification();
                                        notification.setUserId(resident.getUserId());
                                        notification.setTitle("New Community Event: " + event.getTitle());
                                        notification.setMessage(
                                                "A new community event has been created on "
                                                        + event.getEventDate()
                                                        + ". Don't forget to RSVP!");
                                        notification.setCreatedAt(LocalDateTime.now());
                                        notification.setRead(false);
                                        return notification;
                                    })
                            .toList();

            notificationRepository.saveAll(notifications);
            logger.info(
                    "Created {} event notifications for apartmentId: {}",
                    notifications.size(),
                    event.getApartmentId());

        } catch (Exception e) {
            logger.error("Error creating notifications for event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "rsvp-confirmed",
            groupId = "notification-rsvp-group",
            containerFactory = "rsvpKafkaListenerContainerFactory")
    public void consumeRsvpConfirmed(RsvpConfirmedEvent event) {
        logger.info("Received rsvp-confirmed event for residentId: {}", event.getResidentId());

        try {
            Notification notification = new Notification();
            notification.setUserId(event.getResidentId());
            notification.setTitle("RSVP Confirmed: " + event.getEventTitle());
            notification.setMessage(
                    "Your spot is confirmed for "
                            + event.getEventTitle()
                            + " on "
                            + event.getEventDate()
                            + ".");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);

            notificationRepository.save(notification);
            logger.info("Saved RSVP notification for residentId: {}", event.getResidentId());
        } catch (Exception e) {
            logger.error("Failed to save RSVP confirmation notification: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "rsvp-cancelled",
            groupId = "notification-rsvp-cancelled-group",
            containerFactory = "rsvpCancelledKafkaListenerContainerFactory")
    public void consumeRsvpCancelled(RsvpCancelledEvent event) {
        logger.info("Received rsvp-cancelled event for residentId: {}", event.getResidentId());

        try {
            Notification notification = new Notification();
            notification.setUserId(event.getResidentId());
            notification.setTitle("RSVP Cancelled: " + event.getEventTitle());
            notification.setMessage(
                    "Your RSVP for "
                            + event.getEventTitle()
                            + " scheduled on "
                            + event.getEventDate()
                            + " has been cancelled.");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);

            notificationRepository.save(notification);
            logger.info(
                    "Saved RSVP cancellation notification for residentId: {}", event.getResidentId());
        } catch (Exception e) {
            logger.error("Failed to save RSVP cancellation notification: {}", e.getMessage(), e);
        }
    }
}