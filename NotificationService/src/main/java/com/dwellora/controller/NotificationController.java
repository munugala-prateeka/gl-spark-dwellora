package com.dwellora.controller;

import com.dwellora.dto.NotificationResponseDTO;
import com.dwellora.service.NotificationService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing notifications.
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    /**
     * Retrieves all notifications for a given user ID.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserNotifications(userId));
    }

    /**
     * Marks a notification as read by its ID.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAsRead(id));
    }
}