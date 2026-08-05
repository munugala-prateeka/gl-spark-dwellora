package com.dwellora.service;

import com.dwellora.dto.ComplaintRequestDTO;
import com.dwellora.dto.ComplaintResponseDTO;

import java.util.List;

public interface ComplaintService {

    ComplaintResponseDTO raiseComplaint(Integer userId, ComplaintRequestDTO request);

    List<ComplaintResponseDTO> getComplaintsByUser(Integer userId);

    List<ComplaintResponseDTO> getComplaintsByApartment(Integer apartmentId);
}