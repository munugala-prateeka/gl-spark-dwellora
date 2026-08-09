package com.dwellora.service.impl;

import com.dwellora.client.UserClient;
import com.dwellora.dto.ComplaintRequestDTO;
import com.dwellora.dto.ComplaintResponseDTO;
import com.dwellora.dto.ComplaintUpdateDTO;
import com.dwellora.dto.UserDTO;
import com.dwellora.entity.Complaint;
import com.dwellora.enums.ComplaintStatus;
import com.dwellora.event.ComplaintCreatedEvent;
import com.dwellora.event.ComplaintUpdatedEvent;
import com.dwellora.exception.ComplaintException;
import com.dwellora.repository.ComplaintRepository;
import com.dwellora.service.ComplaintService;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service implementation handling complaint business logic and event publication. */
@Service
public class ComplaintServiceImpl implements ComplaintService {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintServiceImpl.class);

    private final ComplaintRepository repository;
    private final UserClient userClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ComplaintServiceImpl(
            ComplaintRepository repository,
            UserClient userClient,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.userClient = userClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public ComplaintResponseDTO raiseComplaint(
            Long userId, Long apartmentId, ComplaintRequestDTO request) {
        UserDTO user;
        try {
            user = userClient.getUserById(userId);
        } catch (Exception e) {
            logger.error("Failed to fetch user details for userId {}: {}", userId, e.getMessage());
            throw new ComplaintException("Unable to verify resident details.");
        }

        if (user == null) {
            throw new ComplaintException("Resident not found with ID: " + userId);
        }

        if (user.getApartmentId() == null || !user.getApartmentId().equals(apartmentId)) {
            throw new ComplaintException("Resident does not belong to this apartment.");
        }

        if (!"RESIDENT".equalsIgnoreCase(user.getRole())) {
            throw new ComplaintException("Only residents can raise complaints.");
        }

        Complaint complaint = new Complaint();
        complaint.setApartmentId(apartmentId);
        complaint.setUserId(userId);
        complaint.setFlatNumber(user.getFlatNumber());
        complaint.setCategory(request.getCategory());
        complaint.setDescription(request.getDescription());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.setRaisedAt(LocalDateTime.now());

        Complaint saved = repository.save(complaint);

        try {
            ComplaintCreatedEvent createdEvent =
                    new ComplaintCreatedEvent(
                            saved.getUserId(), saved.getCategory(), saved.getFlatNumber());

            kafkaTemplate.send("complaint-created", createdEvent);
            logger.info("Published complaint-created event for complaintId: {}", saved.getComplaintId());
        } catch (Exception e) {
            logger.error("Failed to publish complaint-created event: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    @Override
    public List<ComplaintResponseDTO> getComplaintsByUser(Long userId) {
        return repository.findByUserId(userId).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<ComplaintResponseDTO> getComplaintsByApartment(Long apartmentId) {
        return repository.findByApartmentIdOrderByRaisedAtDesc(apartmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ComplaintResponseDTO updateComplaint(Long complaintId, ComplaintUpdateDTO request) {
        Complaint complaint =
                repository
                        .findById(complaintId)
                        .orElseThrow(
                                () -> new ComplaintException("Complaint not found with ID: " + complaintId));

        ComplaintStatus oldStatus = complaint.getStatus();
        ComplaintStatus newStatus = request.getStatus();

        complaint.setStatus(newStatus);

        if (request.getResolutionRemark() != null && !request.getResolutionRemark().isBlank()) {
            complaint.setResolutionRemark(request.getResolutionRemark());
        }

        if (newStatus == ComplaintStatus.RESOLVED) {
            if (complaint.getResolvedAt() == null) {
                complaint.setResolvedAt(LocalDateTime.now());
            }
        } else {
            complaint.setResolvedAt(null);
        }

        Complaint saved = repository.save(complaint);

        try {
            ComplaintUpdatedEvent event =
                    new ComplaintUpdatedEvent(
                            saved.getUserId(),
                            saved.getCategory(),
                            saved.getStatus().name(),
                            saved.getResolutionRemark());

            kafkaTemplate.send("complaint-updated", event);
            logger.info(
                    "Published complaint-updated event for complaintId: {}. {} -> {}",
                    saved.getComplaintId(),
                    oldStatus,
                    newStatus);
        } catch (Exception e) {
            logger.error("Failed to publish complaint-updated event: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    private ComplaintResponseDTO mapToResponse(Complaint complaint) {
        return new ComplaintResponseDTO(
                complaint.getComplaintId(),
                complaint.getApartmentId(),
                complaint.getUserId(),
                complaint.getFlatNumber(),
                complaint.getCategory(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getResolutionRemark(),
                complaint.getRaisedAt(),
                complaint.getResolvedAt());
    }
}