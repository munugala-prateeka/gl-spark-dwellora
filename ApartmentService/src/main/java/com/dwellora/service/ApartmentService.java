package com.dwellora.service;

import com.dwellora.dto.ApartmentRequestDTO;
import com.dwellora.dto.ApartmentResponseDTO;
import java.util.List;

public interface ApartmentService {

    ApartmentResponseDTO addApartment(ApartmentRequestDTO dto);

    List<ApartmentResponseDTO> getAllApartments();

    ApartmentResponseDTO getApartmentById(Integer apartmentId);

    ApartmentResponseDTO updateApartment(
            Integer apartmentId, ApartmentRequestDTO dto);

    void deleteApartment(Integer apartmentId);
}