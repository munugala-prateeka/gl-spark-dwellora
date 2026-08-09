package com.dwellora.repository;

import com.dwellora.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Notification} persistence operations.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Retrieves all notifications for a given user ordered by creation date in descending order.
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}