package com.dwellora.kafka;

import com.dwellora.event.ApartmentCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ApartmentProducer {

    private static final String TOPIC = "apartment-created";

    private final KafkaTemplate<String, ApartmentCreatedEvent> kafkaTemplate;

    public ApartmentProducer(KafkaTemplate<String, ApartmentCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ApartmentCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event);
        System.out.println("Apartment Created Event Published");
    }
}