package com.dwellora.kafka;

import com.dwellora.event.ApartmentCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ApartmentProducer {

    private static final String TOPIC = "apartment-created";

    private static final Logger logger = LoggerFactory.getLogger(CommunityConsumer.class);

    private final KafkaTemplate<String, ApartmentCreatedEvent> kafkaTemplate;

    public ApartmentProducer(KafkaTemplate<String, ApartmentCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ApartmentCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event);
        logger.info("Apartment Created Event Published");
    }
}