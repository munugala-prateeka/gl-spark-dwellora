package com.dwellora.kafka;

import com.dwellora.event.CommunityApprovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommunityProducer {

    private static final Logger logger = LoggerFactory.getLogger(CommunityProducer.class);
    private static final String TOPIC = "community-approved";

    private final KafkaTemplate<String, CommunityApprovedEvent> kafkaTemplate;

    public CommunityProducer(KafkaTemplate<String, CommunityApprovedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(CommunityApprovedEvent event) {
        kafkaTemplate.send(TOPIC, event);
        logger.info("Community Event Published");
    }
}