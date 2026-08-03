package com.dwellora.service.impl;

import com.dwellora.dto.OnboardingRequestDTO;
import com.dwellora.dto.OnboardingResponseDTO;
import com.dwellora.entity.OnboardingRequest;
import com.dwellora.enums.OnboardingStatus;
import com.dwellora.exception.OnboardingException;
import com.dwellora.repository.OnboardingRepository;
import com.dwellora.service.OnboardingService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OnboardingServiceImpl implements OnboardingService {

    private final OnboardingRepository repository;

    public OnboardingServiceImpl(OnboardingRepository repository) {
        this.repository = repository;
    }

    @Override
    public OnboardingResponseDTO createRequest(OnboardingRequestDTO dto) {
        OnboardingRequest request = mapToEntity(dto);
        request.setStatus(OnboardingStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        OnboardingRequest saved = repository.save(request);
        return mapToResponse(saved);
    }

    @Override
    public List<OnboardingResponseDTO> getPendingRequests() {
        return repository.findByStatus(OnboardingStatus.PENDING).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OnboardingResponseDTO approveRequest(Integer requestId) {
        OnboardingRequest request =
                repository
                        .findById(requestId)
                        .orElseThrow(
                                () ->
                                        new OnboardingException(
                                                "Onboarding request not found with id : " + requestId));

        request.setStatus(OnboardingStatus.APPROVED);
        request.setApprovedAt(LocalDateTime.now());
        OnboardingRequest saved = repository.save(request);

        return mapToResponse(saved);
    }

    @Override
    public OnboardingResponseDTO rejectRequest(Integer requestId) {
        OnboardingRequest request =
                repository
                        .findById(requestId)
                        .orElseThrow(
                                () ->
                                        new OnboardingException(
                                                "Onboarding request not found with id : " + requestId));

        request.setStatus(OnboardingStatus.REJECTED);
        OnboardingRequest saved = repository.save(request);

        return mapToResponse(saved);
    }

    private OnboardingResponseDTO mapToResponse(OnboardingRequest request) {
        OnboardingResponseDTO response = new OnboardingResponseDTO();
        response.setRequestId(request.getRequestId());
        response.setApartmentName(request.getApartmentName());
        response.setAddress(request.getAddress());
        response.setCity(request.getCity());
        response.setState(request.getState());
        response.setPincode(request.getPincode());
        response.setTotalBlocks(request.getTotalBlocks());
        response.setTotalUnits(request.getTotalUnits());
        response.setManagerName(request.getManagerName());
        response.setManagerEmail(request.getManagerEmail());
        response.setManagerPhone(request.getManagerPhone());
        response.setStatus(request.getStatus());
        response.setCreatedAt(request.getCreatedAt());
        response.setApprovedAt(request.getApprovedAt());
        return response;
    }

    private OnboardingRequest mapToEntity(OnboardingRequestDTO dto) {
        OnboardingRequest request = new OnboardingRequest();
        request.setApartmentName(dto.getApartmentName());
        request.setAddress(dto.getAddress());
        request.setCity(dto.getCity());
        request.setState(dto.getState());
        request.setPincode(dto.getPincode());
        request.setTotalBlocks(dto.getTotalBlocks());
        request.setTotalUnits(dto.getTotalUnits());
        request.setManagerName(dto.getManagerName());
        request.setManagerEmail(dto.getManagerEmail());
        request.setManagerPhone(dto.getManagerPhone());
        return request;
    }
}