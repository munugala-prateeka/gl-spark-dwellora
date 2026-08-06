package com.dwellora.service;

import com.dwellora.dto.AmenityRequestDTO;
import com.dwellora.dto.AmenityResponseDTO;
import java.util.List;

public interface AmenityService {

    AmenityResponseDTO addAmenity(AmenityRequestDTO request);

    List<AmenityResponseDTO> getAllAmenities();

    AmenityResponseDTO getAmenityById(Integer amenityId);

    AmenityResponseDTO updateAmenity(Integer amenityId, AmenityRequestDTO request);

    void deleteAmenity(Integer amenityId);

    List<AmenityResponseDTO> getAmenitiesByApartment(Integer apartmentId);
}