package com.dwellora.kafka;

import com.dwellora.entity.Apartment;
import com.dwellora.enums.Status;
import com.dwellora.event.ApartmentCreatedEvent;
import com.dwellora.event.CommunityApprovedEvent;
import com.dwellora.repository.ApartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service component responsible for consuming community approval events from Kafka and creating apartments.
 */
@Service
public class CommunityConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CommunityConsumer.class);

    private final ApartmentRepository repository;
    private final ApartmentProducer producer;

    public CommunityConsumer(ApartmentRepository repository, ApartmentProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    /**
     * Listens to the community-approved Kafka topic to process approved events, persist new apartments, and publish creation events.
     */
    @KafkaListener(topics = "community-approved", groupId = "apartment-group")
    public void consume(CommunityApprovedEvent event) {
        logger.info("========== RECEIVED ==========");

        Apartment apartment = new Apartment();
        apartment.setApartmentName(event.getApartmentName());
        apartment.setAddress(event.getAddress());
        apartment.setCity(event.getCity());
        apartment.setState(event.getState());
        apartment.setPincode(event.getPincode());
        apartment.setTotalBlocks(event.getTotalBlocks());
        apartment.setTotalUnits(event.getTotalUnits());
        apartment.setStatus(Status.ACTIVE);

        repository.save(apartment);

        ApartmentCreatedEvent created = new ApartmentCreatedEvent();
        created.setApartmentId(apartment.getApartmentId());
        created.setRequestId(event.getRequestId());
        created.setManagerName(event.getManagerName());
        created.setManagerEmail(event.getManagerEmail());
        created.setManagerPhone(event.getManagerPhone());

        producer.publish(created);

        logger.info("Apartment Created : " + apartment.getApartmentId());
    }
}