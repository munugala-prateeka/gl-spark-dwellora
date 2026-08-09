package com.dwellora.controller;

import com.dwellora.dto.AmenityRequestDTO;
import com.dwellora.dto.AmenityResponseDTO;
import com.dwellora.enums.AmenityType;
import com.dwellora.service.AmenityService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for handling amenity operations and management endpoints. */
@RestController
@RequestMapping("/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    /** Creates a new amenity for an apartment. Restricted to managers. */
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<AmenityResponseDTO> addAmenity(
            @RequestHeader("X-Apartment-Id") Long apartmentId,
            @Valid @RequestBody AmenityRequestDTO request) {
        AmenityResponseDTO response = amenityService.addAmenity(apartmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Retrieves all amenities belonging to the requester's apartment. */
    @GetMapping
    public ResponseEntity<List<AmenityResponseDTO>> getAmenities(
            @RequestHeader("X-Apartment-Id") Long apartmentId) {
        return ResponseEntity.ok(amenityService.getAmenitiesByApartment(apartmentId));
    }

    /** Retrieves details for a single amenity by ID. */
    @GetMapping("/{id}")
    public ResponseEntity<AmenityResponseDTO> getAmenityById(
            @PathVariable Long id,
            @RequestHeader("X-Apartment-Id") Long apartmentId) {
        return ResponseEntity.ok(amenityService.getAmenityById(id, apartmentId));
    }

    /** Updates an existing amenity. Restricted to managers. */
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<AmenityResponseDTO> updateAmenity(
            @PathVariable Long id,
            @RequestHeader("X-Apartment-Id") Long apartmentId,
            @Valid @RequestBody AmenityRequestDTO request) {
        return ResponseEntity.ok(amenityService.updateAmenity(apartmentId, id, request));
    }

    /** Deletes an amenity by ID. Restricted to managers. */
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmenity(
            @PathVariable Long id,
            @RequestHeader("X-Apartment-Id") Long apartmentId) {
        amenityService.deleteAmenity(id, apartmentId);
        return ResponseEntity.noContent().build();
    }

    /** Endpoint to retrieve amenities for a specific apartment ID. */
    @GetMapping("/apartment/{id}")
    public ResponseEntity<List<AmenityResponseDTO>> getAmenitiesByApartment(
            @PathVariable Long id) {
        return ResponseEntity.ok(amenityService.getAmenitiesByApartment(id));
    }

    /** Retrieves all available amenity types. */
    @GetMapping("/types")
    public ResponseEntity<AmenityType[]> getAmenityTypes() {
        return ResponseEntity.ok(AmenityType.values());
    }
}