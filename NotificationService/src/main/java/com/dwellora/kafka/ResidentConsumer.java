package com.dwellora.kafka;

import com.dwellora.event.ResidentCreatedEvent;
import com.dwellora.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ResidentConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ResidentConsumer.class);
    private final EmailService emailService;

    public ResidentConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = "resident-created",
            groupId = "resident-notification-group",
            containerFactory = "residentKafkaListenerContainerFactory")
    public void consume(ResidentCreatedEvent event) {
        logger.info("Resident Created Event Received for: {}", event.getResidentEmail());
        emailService.sendResidentWelcomeEmail(
                event.getResidentName(), event.getResidentEmail(), event.getActivationToken());
    }
}