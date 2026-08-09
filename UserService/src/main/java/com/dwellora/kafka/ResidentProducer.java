package com.dwellora.kafka;

import com.dwellora.event.ResidentCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for publishing resident creation events to Kafka.
 */
@Service
public class ResidentProducer {

    private static final Logger logger = LoggerFactory.getLogger(ResidentProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ResidentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a resident created event to the Kafka topic.
     */
    public void publish(ResidentCreatedEvent event) {
        kafkaTemplate.send("resident-created", event);
        logger.info("Resident Created Event Published");
    }
}