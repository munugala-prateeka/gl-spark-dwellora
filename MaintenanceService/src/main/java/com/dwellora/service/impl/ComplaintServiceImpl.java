package com.dwellora.service.impl;

import com.dwellora.client.UserClient;
import com.dwellora.dto.ComplaintRequestDTO;
import com.dwellora.dto.ComplaintResponseDTO;
import com.dwellora.dto.UserDTO;
import com.dwellora.entity.Complaint;
import com.dwellora.enums.ComplaintStatus;
import com.dwellora.exception.ComplaintException;
import com.dwellora.repository.ComplaintRepository;
import com.dwellora.service.ComplaintService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository repository;
    private final UserClient userClient;

    public ComplaintServiceImpl(ComplaintRepository repository, UserClient userClient) {
        this.repository = repository;
        this.userClient = userClient;
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

    private ComplaintResponseDTO mapToResponse(Complaint c) {
        return new ComplaintResponseDTO(
                c.getComplaintId(), c.getApartmentId(), c.getUserId(), c.getFlatNumber(),
                c.getCategory(), c.getDescription(), c.getStatus(),
                c.getResolutionRemark(), c.getRaisedAt(), c.getResolvedAt());
    }
}