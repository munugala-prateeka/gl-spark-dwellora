package com.dwellora.controller;

import com.dwellora.dto.ApartmentRequestDTO;
import com.dwellora.dto.ApartmentResponseDTO;
import com.dwellora.service.ApartmentService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing apartment resources.
 */
@RestController
@RequestMapping("/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    /**
     * Adds a new apartment.
     */
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @PostMapping
    public ResponseEntity<ApartmentResponseDTO> addApartment(
            @Valid @RequestBody ApartmentRequestDTO dto) {
        ApartmentResponseDTO response = apartmentService.addApartment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all apartments.
     */
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @GetMapping
    public ResponseEntity<List<ApartmentResponseDTO>> getAllApartments() {
        return ResponseEntity.ok(apartmentService.getAllApartments());
    }

    /**
     * Retrieves an apartment by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApartmentResponseDTO> getApartment(@PathVariable Long id) {
        return ResponseEntity.ok(apartmentService.getApartmentById(id));
    }

    /**
     * Updates an existing apartment by its ID.
     */
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApartmentResponseDTO> updateApartment(
            @PathVariable Long id, @Valid @RequestBody ApartmentRequestDTO dto) {
        return ResponseEntity.ok(apartmentService.updateApartment(id, dto));
    }

    /**
     * Deletes an apartment by its ID.
     */
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApartment(@PathVariable Long id) {
        apartmentService.deleteApartment(id);
        return ResponseEntity.noContent().build();
    }
}