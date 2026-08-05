package com.dwellora.service;

import com.dwellora.dto.NotificationResponseDTO;
import java.util.List;

public interface NotificationService {

    List<NotificationResponseDTO> getUserNotifications(Integer userId);

    NotificationResponseDTO markAsRead(Integer notificationId);
}