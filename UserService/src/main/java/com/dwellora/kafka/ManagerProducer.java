package com.dwellora.kafka;

import com.dwellora.event.ManagerCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ManagerProducer {

    private static final Logger logger = LoggerFactory.getLogger(ManagerProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ManagerProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ManagerCreatedEvent event) {
        kafkaTemplate.send("manager-created", event);
        logger.info("Manager Created Event Published");
    }
}