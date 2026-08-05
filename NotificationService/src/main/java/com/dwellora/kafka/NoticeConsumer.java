package com.dwellora.kafka;

import com.dwellora.client.UserClient;
import com.dwellora.dto.UserResponseDTO;
import com.dwellora.entity.Notification;
import com.dwellora.enums.NotificationType;
import com.dwellora.event.NoticePublishedEvent;
import com.dwellora.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NoticeConsumer.class);

    private final NotificationRepository repository;
    private final UserClient userClient;

    public NoticeConsumer(NotificationRepository repository, UserClient userClient) {
        this.repository = repository;
        this.userClient = userClient;
    }

    @KafkaListener(topics = "notice-published", groupId = "notification-group",
            containerFactory = "noticeKafkaListenerContainerFactory")
    @Transactional
    public void consumeNotice(NoticePublishedEvent event) {
        logger.info("Received notice-published event for apartmentId: {}", event.getApartmentId());

        try {
            List<UserResponseDTO> residents = userClient.getResidentsByApartment(event.getApartmentId());
            for (UserResponseDTO resident : residents) {
                Notification notification = new Notification();
                notification.setUserId(resident.getUserId());
                notification.setType(NotificationType.NOTICE);
                notification.setTitle((Boolean.TRUE.equals(event.getIsUrgent()) ? "[URGENT] " : "") + event.getTitle());
                notification.setMessage(event.getBody());
                notification.setRead(false);
                notification.setCreatedAt(LocalDateTime.now());

                repository.save(notification);
            }
            logger.info("Saved notice notifications for {} residents of apartment {}", residents.size(), event.getApartmentId());
        } catch (Exception e) {
            logger.error("Error creating notifications for published notice: {}", e.getMessage());
        }
    }
}