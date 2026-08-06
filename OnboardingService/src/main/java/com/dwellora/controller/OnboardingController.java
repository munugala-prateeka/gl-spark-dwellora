package com.dwellora.controller;

import com.dwellora.dto.OnboardingRequestDTO;
import com.dwellora.dto.OnboardingResponseDTO;
import com.dwellora.service.OnboardingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<OnboardingResponseDTO> createRequest(@Valid @RequestBody OnboardingRequestDTO request) {
        OnboardingResponseDTO response = service.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<OnboardingResponseDTO>> pendingRequests() {
        return ResponseEntity.ok(service.getPendingRequests());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<OnboardingResponseDTO> approveRequest(@PathVariable Integer id) {
        return ResponseEntity.ok(service.approveRequest(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<OnboardingResponseDTO> rejectRequest(@PathVariable Integer id) {
        return ResponseEntity.ok(service.rejectRequest(id));
    }

    @GetMapping
    public List<OnboardingResponseDTO> getAllRequests() {
        return service.getAllRequests();
    }
}