package com.dwellora.kafka;

import com.dwellora.entity.Notification;
import com.dwellora.enums.NotificationType;
import com.dwellora.event.ComplaintCreatedEvent;
import com.dwellora.event.ComplaintUpdatedEvent;
import com.dwellora.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ComplaintConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintConsumer.class);
    private final NotificationRepository repository;

    public ComplaintConsumer(NotificationRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "complaint-created", groupId = "notification-group",
            containerFactory = "complaintCreatedKafkaListenerContainerFactory")
    @Transactional
    public void consumeCreated(ComplaintCreatedEvent event) {
        logger.info("Received complaint created event for userId: {}", event.getUserId());

        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setType(NotificationType.COMPLAINT);
        notification.setTitle("Complaint Registered");
        notification.setMessage("Your complaint regarding '" + event.getCategory() + "' for flat "
                + event.getFlatNumber() + " has been successfully submitted and is currently OPEN.");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        repository.save(notification);
        logger.info("Saved complaint creation notification for user {}", event.getUserId());
    }

    @KafkaListener(topics = "complaint-updated", groupId = "notification-group",
            containerFactory = "complaintKafkaListenerContainerFactory")
    @Transactional
    public void consume(ComplaintUpdatedEvent event) {
        logger.info("Received complaint update event for userId: {}", event.getUserId());

        String message = buildNotificationMessage(event);

        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setType(NotificationType.COMPLAINT);
        notification.setTitle("Complaint Status: " + event.getStatus());
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        repository.save(notification);
        logger.info("Saved complaint notification for user {}", event.getUserId());
    }

    private String buildNotificationMessage(ComplaintUpdatedEvent event) {
        String baseMessage = "Your complaint regarding '" + event.getCategory() + "' status has been changed to " + event.getStatus() + ".";

        if ("IN_PROGRESS".equalsIgnoreCase(event.getStatus())) {
            baseMessage += " Maintenance team is working on it.";
        } else if ("RESOLVED".equalsIgnoreCase(event.getStatus())) {
            baseMessage += " Issue resolved successfully.";
        }

        if (event.getResolutionRemark() != null && !event.getResolutionRemark().isBlank()) {
            baseMessage += " Note: " + event.getResolutionRemark();
        }

        return baseMessage;
    }
}