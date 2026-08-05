package com.dwellora.kafka;

import com.dwellora.event.BookingCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingProducer {

    private static final Logger logger = LoggerFactory.getLogger(BookingProducer.class);
    private static final String TOPIC = "booking-created";

    private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;

    public BookingProducer(
            KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBookingCreatedEvent(
            BookingCreatedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event
        );

        logger.info(
                "Published Booking Event : "
                        + event.getBookingId());

    }

}