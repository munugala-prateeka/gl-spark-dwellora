package com.dwellora.controller;

import com.dwellora.dto.ComplaintRequestDTO;
import com.dwellora.dto.ComplaintResponseDTO;
import com.dwellora.dto.ComplaintUpdateDTO;
import com.dwellora.service.ComplaintService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for managing complaint lifecycle and endpoints. */
@RestController
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    /** Submits a new complaint on behalf of a resident. */
    @PreAuthorize("hasRole('RESIDENT')")
    @PostMapping
    public ResponseEntity<ComplaintResponseDTO> raiseComplaint(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Apartment-Id") Long apartmentId,
            @Valid @RequestBody ComplaintRequestDTO request) {
        ComplaintResponseDTO response =
                complaintService.raiseComplaint(userId, apartmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Retrieves complaints submitted by the authenticated resident. */
    @PreAuthorize("hasRole('RESIDENT')")
    @GetMapping("/my")
    public ResponseEntity<List<ComplaintResponseDTO>> getMyComplaints(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(complaintService.getComplaintsByUser(userId));
    }

    /** Retrieves all complaints registered for an apartment complex. */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<List<ComplaintResponseDTO>> getComplaintsByApartment(
            @RequestHeader("X-Apartment-Id") Long apartmentId) {
        return ResponseEntity.ok(complaintService.getComplaintsByApartment(apartmentId));
    }

    /** Updates complaint status and resolution remarks. Restricted to managers. */
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ComplaintResponseDTO> updateComplaint(
            @PathVariable Long id, @Valid @RequestBody ComplaintUpdateDTO request) {
        return ResponseEntity.ok(complaintService.updateComplaint(id, request));
    }
}