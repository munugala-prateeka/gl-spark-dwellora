package com.dwellora.service.impl;

import com.dwellora.client.UserClient;
import com.dwellora.dto.*;
import com.dwellora.entity.Complaint;
import com.dwellora.enums.ComplaintStatus;
import com.dwellora.event.ComplaintCreatedEvent;
import com.dwellora.event.ComplaintUpdatedEvent;
import com.dwellora.exception.ComplaintException;
import com.dwellora.repository.ComplaintRepository;
import com.dwellora.service.ComplaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintServiceImpl.class);

    private final ComplaintRepository repository;
    private final UserClient userClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ComplaintServiceImpl(ComplaintRepository repository,
                                UserClient userClient,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.userClient = userClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public ComplaintResponseDTO raiseComplaint(Integer userId, ComplaintRequestDTO request) {
        UserDTO user;
        try {
            user = userClient.getUserById(userId);
        } catch (Exception e) {
            throw new ComplaintException("Unable to verify resident details.");
        }
        if (user == null) {
            throw new ComplaintException("Resident not found with ID: " + userId);
        }

        Complaint complaint = new Complaint();
        complaint.setApartmentId(request.getApartmentId());
        complaint.setUserId(userId);
        complaint.setFlatNumber(user.getFlatNumber());
        complaint.setCategory(request.getCategory());
        complaint.setDescription(request.getDescription());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.setRaisedAt(LocalDateTime.now());

        Complaint saved = repository.save(complaint);

        try {
            ComplaintCreatedEvent createdEvent = new ComplaintCreatedEvent(
                    saved.getUserId(),
                    saved.getCategory(),
                    saved.getFlatNumber()
            );
            kafkaTemplate.send("complaint-created", createdEvent);
            logger.info("Published complaint-created event for complaintId: {}", saved.getComplaintId());
        } catch (Exception e) {
            logger.error("Failed to publish complaint created event to Kafka: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    @Override
    public List<ComplaintResponseDTO> getComplaintsByUser(Integer userId) {
        return repository.findByUserId(userId).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<ComplaintResponseDTO> getComplaintsByApartment(Integer apartmentId) {
        return repository.findByApartmentIdOrderByRaisedAtDesc(apartmentId).stream()
                .map(this::mapToResponse).toList();
    }

    @Override
    public ComplaintResponseDTO updateComplaint(Integer complaintId, ComplaintUpdateDTO request) {
        Complaint complaint = repository.findById(complaintId)
                .orElseThrow(() -> new ComplaintException("Complaint not found with ID: " + complaintId));

        complaint.setStatus(request.getStatus());
        if (request.getResolutionRemark() != null) {
            complaint.setResolutionRemark(request.getResolutionRemark());
        }
        if (request.getStatus() == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        Complaint saved = repository.save(complaint);

        try {
            ComplaintUpdatedEvent event = new ComplaintUpdatedEvent(
                    saved.getUserId(),
                    saved.getCategory(),
                    saved.getStatus().name(),
                    saved.getResolutionRemark()
            );
            kafkaTemplate.send("complaint-updated", event);
            logger.info("Published complaint-updated event for complaintId: {}", saved.getComplaintId());
        } catch (Exception e) {
            logger.error("Failed to publish complaint update event to Kafka: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    private ComplaintResponseDTO mapToResponse(Complaint c) {
        return new ComplaintResponseDTO(
                c.getComplaintId(), c.getApartmentId(), c.getUserId(), c.getFlatNumber(),
                c.getCategory(), c.getDescription(), c.getStatus(),
                c.getResolutionRemark(), c.getRaisedAt(), c.getResolvedAt());
    }
}