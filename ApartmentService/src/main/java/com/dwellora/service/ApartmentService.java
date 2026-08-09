package com.dwellora.service;

import com.dwellora.dto.ApartmentRequestDTO;
import com.dwellora.dto.ApartmentResponseDTO;
import java.util.List;

/**
 * Service interface for managing apartment operations.
 */
public interface ApartmentService {

    /**
     * Adds a new apartment.
     */
    ApartmentResponseDTO addApartment(ApartmentRequestDTO dto);

    /**
     * Retrieves all apartments.
     */
    List<ApartmentResponseDTO> getAllApartments();

    /**
     * Retrieves an apartment by its ID.
     */
    ApartmentResponseDTO getApartmentById(Long apartmentId);

    /**
     * Updates an existing apartment by its ID.
     */
    ApartmentResponseDTO updateApartment(Long apartmentId, ApartmentRequestDTO dto);

    /**
     * Deletes an apartment by its ID.
     */
    void deleteApartment(Long apartmentId);
}