package com.dwellora.service;

import com.dwellora.dto.OnboardingRequestDTO;
import com.dwellora.dto.OnboardingResponseDTO;
import java.util.List;

public interface OnboardingService {

    OnboardingResponseDTO createRequest(OnboardingRequestDTO request);

    List<OnboardingResponseDTO> getPendingRequests();

    OnboardingResponseDTO approveRequest(Integer requestId);

    OnboardingResponseDTO rejectRequest(Integer requestId);

    List<OnboardingResponseDTO> getAllRequests();
}