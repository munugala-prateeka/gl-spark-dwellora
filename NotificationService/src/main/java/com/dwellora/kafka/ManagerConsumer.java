package com.dwellora.kafka;

import com.dwellora.event.ManagerCreatedEvent;
import com.dwellora.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ManagerConsumer {

    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(ManagerConsumer.class);

    public ManagerConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "manager-created", groupId = "manager-notification-group", containerFactory = "managerKafkaListenerContainerFactory")
    public void consume(ManagerCreatedEvent event) {
        logger.info("Manager Created Event Received");
        emailService.sendManagerWelcomeEmail(event.getManagerName(), event.getManagerEmail(), event.getActivationToken());
    }
}