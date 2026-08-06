package com.dwellora.controller;

import com.dwellora.dto.ComplaintRequestDTO;
import com.dwellora.dto.ComplaintResponseDTO;
import com.dwellora.dto.ComplaintUpdateDTO;
import com.dwellora.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<ComplaintResponseDTO> raiseComplaint(
            @RequestParam Integer userId,
            @Valid @RequestBody ComplaintRequestDTO request) {
        ComplaintResponseDTO response = complaintService.raiseComplaint(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ComplaintResponseDTO>> getComplaintsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(complaintService.getComplaintsByUser(userId));
    }

    @GetMapping("/apartment/{apartmentId}")
    public ResponseEntity<List<ComplaintResponseDTO>> getComplaintsByApartment(@PathVariable Integer apartmentId) {
        return ResponseEntity.ok(complaintService.getComplaintsByApartment(apartmentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintResponseDTO> updateComplaint(
            @PathVariable Integer id,
            @Valid @RequestBody ComplaintUpdateDTO request) {
        return ResponseEntity.ok(complaintService.updateComplaint(id, request));
    }
}