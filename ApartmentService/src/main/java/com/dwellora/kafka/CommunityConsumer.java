package com.dwellora.kafka;

import com.dwellora.entity.Apartment;
import com.dwellora.enums.Status;
import com.dwellora.event.ApartmentCreatedEvent;
import com.dwellora.event.CommunityApprovedEvent;
import com.dwellora.repository.ApartmentRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CommunityConsumer {

    private final ApartmentRepository repository;
    private final ApartmentProducer producer;

    public CommunityConsumer(ApartmentRepository repository, ApartmentProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    @KafkaListener(topics = "community-approved", groupId = "apartment-group")
    public void consume(CommunityApprovedEvent event) {
        System.out.println("========== RECEIVED ==========");
        System.out.println(event);

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

        System.out.println("Apartment Created : " + apartment.getApartmentId());
    }
}