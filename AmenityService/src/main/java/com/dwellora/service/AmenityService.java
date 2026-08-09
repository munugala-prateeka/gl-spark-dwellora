package com.dwellora.service;

import com.dwellora.dto.AmenityRequestDTO;
import com.dwellora.dto.AmenityResponseDTO;
import java.util.List;

/** Service interface for amenity management operations. */
public interface AmenityService {

    /** Creates a new amenity for an apartment. */
    AmenityResponseDTO addAmenity(Long apartmentId, AmenityRequestDTO request);

    /** Retrieves all amenities in the system. */
    List<AmenityResponseDTO> getAllAmenities();

    /** Retrieves an amenity by ID for a given apartment. */
    AmenityResponseDTO getAmenityById(Long amenityId, Long apartmentId);

    /** Updates an existing amenity for an apartment. */
    AmenityResponseDTO updateAmenity(
            Long apartmentId, Long amenityId, AmenityRequestDTO request);

    /** Deletes an amenity by ID for a given apartment. */
    void deleteAmenity(Long amenityId, Long apartmentId);

    /** Retrieves all amenities belonging to a specific apartment. */
    List<AmenityResponseDTO> getAmenitiesByApartment(Long apartmentId);
}