package com.dwellora.service;

import com.dwellora.dto.ComplaintRequestDTO;
import com.dwellora.dto.ComplaintResponseDTO;
import com.dwellora.dto.ComplaintUpdateDTO;
import java.util.List;

/** Service interface defining business operations for complaint management. */
public interface ComplaintService {

    /** Submits a complaint on behalf of a verified resident and emits a creation event. */
    ComplaintResponseDTO raiseComplaint(
            Long userId, Long apartmentId, ComplaintRequestDTO request);

    /** Retrieves all complaints created by a specific user. */
    List<ComplaintResponseDTO> getComplaintsByUser(Long userId);

    /** Retrieves all complaints registered for an apartment complex. */
    List<ComplaintResponseDTO> getComplaintsByApartment(Long apartmentId);

    /** Updates complaint status and resolution details, emitting an update event. */
    ComplaintResponseDTO updateComplaint(Long complaintId, ComplaintUpdateDTO request);
}