package com.dwellora.kafka;

import com.dwellora.entity.User;
import com.dwellora.enums.AccountStatus;
import com.dwellora.enums.Role;
import com.dwellora.event.ApartmentCreatedEvent;
import com.dwellora.event.ManagerCreatedEvent;
import com.dwellora.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for processing apartment creation events and initializing manager accounts.
 */
@Service
public class ApartmentCreatedConsumer {

    private final UserRepository repository;
    private final ManagerProducer producer;

    public ApartmentCreatedConsumer(UserRepository repository, ManagerProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    /**
     * Consumes an apartment created event, creates a manager user, and publishes a manager created event.
     */
    @KafkaListener(topics = "apartment-created", groupId = "user-group")
    public void consume(ApartmentCreatedEvent event) {

        User manager = new User();
        manager.setApartmentId(event.getApartmentId());
        manager.setFullName(event.getManagerName());
        manager.setEmail(event.getManagerEmail().trim().toLowerCase());
        manager.setPhone(event.getManagerPhone());
        manager.setFlatNumber("Office");
        manager.setRole(Role.MANAGER);
        manager.setAccountStatus(AccountStatus.PENDING_ACTIVATION);
        manager.setPassword(null);

        String token = UUID.randomUUID().toString();
        manager.setActivationToken(token);
        manager.setActivationTokenExpiry(LocalDateTime.now().plusHours(24));

        repository.save(manager);

        ManagerCreatedEvent created = new ManagerCreatedEvent();
        created.setUserId(manager.getUserId());
        created.setApartmentId(manager.getApartmentId());
        created.setManagerName(manager.getFullName());
        created.setManagerEmail(manager.getEmail());
        created.setActivationToken(token);

        producer.publish(created);
    }
}