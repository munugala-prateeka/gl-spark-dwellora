package com.dwellora.kafka;

import com.dwellora.entity.Notification;
import com.dwellora.enums.NotificationType;
import com.dwellora.event.BookingCreatedEvent;
import com.dwellora.repository.NotificationRepository;
import java.time.LocalDateTime;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingConsumer {

    private final NotificationRepository repository;

    public BookingConsumer(NotificationRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "booking-created",
            groupId = "notification-group",
            containerFactory = "bookingKafkaListenerContainerFactory")
    @Transactional
    public void consume(BookingCreatedEvent event) {
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setType(NotificationType.BOOKING);
        notification.setTitle("Booking Confirmed");
        notification.setMessage(
                "Your booking for "
                        + event.getAmenityName()
                        + " on "
                        + event.getBookingDate()
                        + " from "
                        + event.getStartTime()
                        + " to "
                        + event.getEndTime()
                        + " has been confirmed.");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        repository.save(notification);
    }
}