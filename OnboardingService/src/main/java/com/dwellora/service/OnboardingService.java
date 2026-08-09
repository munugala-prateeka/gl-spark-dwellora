package com.dwellora.service;

import com.dwellora.dto.OnboardingRequestDTO;
import com.dwellora.dto.OnboardingResponseDTO;
import java.util.List;

/**
 * Service interface for handling onboarding requests.
 */
public interface OnboardingService {

    OnboardingResponseDTO createRequest(OnboardingRequestDTO request);

    List<OnboardingResponseDTO> getPendingRequests();

    OnboardingResponseDTO approveRequest(Long requestId);

    OnboardingResponseDTO rejectRequest(Long requestId);

    List<OnboardingResponseDTO> getAllRequests();
}