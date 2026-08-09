package com.dwellora.service;

import com.dwellora.dto.NotificationResponseDTO;
import java.util.List;

/**
 * Service interface defining operations for retrieving and managing user notifications.
 */
public interface NotificationService {

    /**
     * Retrieves all notifications associated with a specific user ID.
     */
    List<NotificationResponseDTO> getUserNotifications(Long userId);

    /**
     * Marks a specific notification as read.
     */
    NotificationResponseDTO markAsRead(Long notificationId);
}