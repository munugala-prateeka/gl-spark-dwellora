package com.dwellora.service.impl;

import com.dwellora.dto.NotificationResponseDTO;
import com.dwellora.entity.Notification;
import com.dwellora.exception.NotificationNotFoundException;
import com.dwellora.repository.NotificationRepository;
import com.dwellora.service.NotificationService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing user notification retrieval and status updates.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all notifications for a given user ordered by creation timestamp descending.
     */
    @Override
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        List<Notification> notifications = repository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(this::mapToResponseDTO).toList();
    }

    /**
     * Marks a specific notification as read.
     */
    @Override
    @Transactional
    public NotificationResponseDTO markAsRead(Long notificationId) {
        Notification notification =
                repository.findById(notificationId).orElseThrow(
                                () -> new NotificationNotFoundException(
                                                "Notification not found with ID: " + notificationId));

        notification.setRead(true);
        Notification saved = repository.save(notification);

        return mapToResponseDTO(saved);
    }

    /**
     * Maps a {@link Notification} entity to its response DTO representation.
     */
    private NotificationResponseDTO mapToResponseDTO(Notification notification) {
        return new NotificationResponseDTO(
                notification.getNotificationId(),
                notification.getUserId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getRead(),
                notification.getCreatedAt());
    }
}