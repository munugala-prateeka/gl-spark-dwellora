package com.dwellora.controller;

import com.dwellora.dto.ComplaintRequestDTO;
import com.dwellora.dto.ComplaintResponseDTO;
import com.dwellora.dto.ComplaintUpdateDTO;
import com.dwellora.service.ComplaintService;
import jakarta.validation.Valid;
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
    public ComplaintResponseDTO raiseComplaint(
            @RequestParam Integer userId,
            @Valid @RequestBody ComplaintRequestDTO request) {
        return complaintService.raiseComplaint(userId, request);
    }

    @GetMapping("/user/{userId}")
    public List<ComplaintResponseDTO> getComplaintsByUser(@PathVariable Integer userId) {
        return complaintService.getComplaintsByUser(userId);
    }

    @GetMapping("/apartment/{apartmentId}")
    public List<ComplaintResponseDTO> getComplaintsByApartment(@PathVariable Integer apartmentId) {
        return complaintService.getComplaintsByApartment(apartmentId);
    }

    @PutMapping("/{id}")
    public ComplaintResponseDTO updateComplaint(
            @PathVariable Integer id,
            @Valid @RequestBody ComplaintUpdateDTO request) {
        return complaintService.updateComplaint(id, request);
    }
}