package com.dwellora.controller;

import com.dwellora.dto.OnboardingRequestDTO;
import com.dwellora.dto.OnboardingResponseDTO;
import com.dwellora.service.OnboardingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onboarding")
public class OnboardingController {

    private final OnboardingService service;

    public OnboardingController(OnboardingService service) {
        this.service = service;
    }

    @PostMapping("/request")
    public OnboardingResponseDTO createRequest(@Valid @RequestBody OnboardingRequestDTO request) {
        return service.createRequest(request);
    }

    @GetMapping("/pending")
    public List<OnboardingResponseDTO> pendingRequests() {
        return service.getPendingRequests();
    }

    @PutMapping("/{id}/approve")
    public OnboardingResponseDTO approveRequest(@PathVariable Integer id) {
        return service.approveRequest(id);
    }

    @PutMapping("/{id}/reject")
    public OnboardingResponseDTO rejectRequest(@PathVariable Integer id) {
        return service.rejectRequest(id);
    }
}