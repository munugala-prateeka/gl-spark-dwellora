package com.dwellora.kafka;

import com.dwellora.entity.User;
import com.dwellora.enums.AccountStatus;
import com.dwellora.enums.Role;
import com.dwellora.event.ApartmentCreatedEvent;
import com.dwellora.event.ManagerCreatedEvent;
import com.dwellora.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ApartmentCreatedConsumer {

    private final UserRepository repository;
    private final ManagerProducer producer;
    private static final Logger logger = LoggerFactory.getLogger(ApartmentCreatedConsumer.class);

    public ApartmentCreatedConsumer(UserRepository repository, ManagerProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    @KafkaListener(topics = "apartment-created", groupId = "user-group")
    public void consume(ApartmentCreatedEvent event) {
        System.out.println("Manager creation event received");

        User manager = new User();
        manager.setApartmentId(event.getApartmentId());
        manager.setFullName(event.getManagerName());
        manager.setEmail(event.getManagerEmail());
        manager.setPhone(event.getManagerPhone());
        manager.setPassword("manager123");
        manager.setFlatNumber("Office");
        manager.setRole(Role.MANAGER);
        manager.setAccountStatus(AccountStatus.ACTIVE);

        repository.save(manager);

        logger.info("Manager Created Successfully");

        ManagerCreatedEvent created = new ManagerCreatedEvent();
        created.setUserId(manager.getUserId());
        created.setApartmentId(manager.getApartmentId());
        created.setManagerName(manager.getFullName());
        created.setManagerEmail(manager.getEmail());

        producer.publish(created);
    }
}