package com.dwellora.service.impl;

import com.dwellora.dto.NotificationResponseDTO;
import com.dwellora.entity.Notification;
import com.dwellora.enums.NotificationType;
import com.dwellora.exception.NotificationNotFoundException;
import com.dwellora.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link NotificationServiceImpl}.
 *
 * <p>Covers US-018 - Resident Views In-App Notifications.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification bookingNotification;

    @BeforeEach
    void setUp() {
        bookingNotification = new Notification();
        bookingNotification.setNotificationId(1L);
        bookingNotification.setUserId(100L);
        bookingNotification.setType(NotificationType.BOOKING);
        bookingNotification.setTitle("Booking confirmed");
        bookingNotification.setMessage("Your Gym slot for tomorrow 7-8am is booked.");
        bookingNotification.setRead(false);
        bookingNotification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("US-018 (AC-1): Given a relevant event occurred, when I open notifications, then it appears in my list")
    void getUserNotifications_ReturnsUsersNotifications() {
        // Given
        when(repository.findByUserIdOrderByCreatedAtDesc(100L)).thenReturn(List.of(bookingNotification));

        // When
        List<NotificationResponseDTO> results = notificationService.getUserNotifications(100L);

        // Then
        assertEquals(1, results.size());
        assertEquals("Booking confirmed", results.get(0).getTitle());
        assertEquals(NotificationType.BOOKING, results.get(0).getType());
    }

    @Test
    @DisplayName("US-018: Given no notifications exist yet, when I open notifications, then an empty list is returned")
    void getUserNotifications_NoNotifications_ReturnsEmptyList() {
        // Given
        when(repository.findByUserIdOrderByCreatedAtDesc(200L)).thenReturn(Collections.emptyList());

        // When
        List<NotificationResponseDTO> results = notificationService.getUserNotifications(200L);

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("US-018 (AC-2): Given an unread notification, when I open it, then it is marked as read")
    void markAsRead_ExistingNotification_MarksRead() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(bookingNotification));
        when(repository.save(bookingNotification)).thenReturn(bookingNotification);

        // When
        NotificationResponseDTO response = notificationService.markAsRead(1L);

        // Then
        assertNotNull(response);
        assertTrue(response.getRead());

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().getRead());
    }

    @Test
    @DisplayName("US-018: Given a non-existent notification id, when marking as read, then a NotificationNotFoundException is thrown")
    void markAsRead_MissingNotification_ThrowsException() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotificationNotFoundException.class, () -> notificationService.markAsRead(999L));
    }
}
