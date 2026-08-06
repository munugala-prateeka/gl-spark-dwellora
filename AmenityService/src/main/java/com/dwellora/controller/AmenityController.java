package com.dwellora.controller;

import com.dwellora.dto.AmenityRequestDTO;
import com.dwellora.dto.AmenityResponseDTO;
import com.dwellora.enums.AmenityType;
import com.dwellora.service.AmenityService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @PostMapping
    public ResponseEntity<AmenityResponseDTO> addAmenity(@Valid @RequestBody AmenityRequestDTO request) {
        AmenityResponseDTO response = amenityService.addAmenity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AmenityResponseDTO>> getAllAmenities() {
        return ResponseEntity.ok(amenityService.getAllAmenities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityResponseDTO> getAmenityById(@PathVariable Integer id) {
        return ResponseEntity.ok(amenityService.getAmenityById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AmenityResponseDTO> updateAmenity(
            @PathVariable Integer id, @Valid @RequestBody AmenityRequestDTO request) {
        return ResponseEntity.ok(amenityService.updateAmenity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmenity(@PathVariable Integer id) {
        amenityService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/apartment/{id}")
    public ResponseEntity<List<AmenityResponseDTO>> getAmenitiesByApartment(@PathVariable Integer id) {
        return ResponseEntity.ok(amenityService.getAmenitiesByApartment(id));
    }

    @GetMapping("/types")
    public ResponseEntity<AmenityType[]> getAmenityTypes() {
        return ResponseEntity.ok(AmenityType.values());
    }
}