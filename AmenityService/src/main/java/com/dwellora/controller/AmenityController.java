package com.dwellora.controller;

import com.dwellora.dto.AmenityRequestDTO;
import com.dwellora.dto.AmenityResponseDTO;
import com.dwellora.enums.AmenityType;
import com.dwellora.service.AmenityService;
import jakarta.validation.Valid;
import java.util.List;
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
    public AmenityResponseDTO addAmenity(@Valid @RequestBody AmenityRequestDTO request) {
        return amenityService.addAmenity(request);
    }

    @GetMapping
    public List<AmenityResponseDTO> getAllAmenities() {
        return amenityService.getAllAmenities();
    }

    @GetMapping("/{id}")
    public AmenityResponseDTO getAmenityById(@PathVariable Integer id) {
        return amenityService.getAmenityById(id);
    }

    @PutMapping("/{id}")
    public AmenityResponseDTO updateAmenity(
            @PathVariable Integer id, @Valid @RequestBody AmenityRequestDTO request) {
        return amenityService.updateAmenity(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteAmenity(@PathVariable Integer id) {
        amenityService.deleteAmenity(id);
    }

    @GetMapping("/apartment/{id}")
    public List<AmenityResponseDTO> getAmenitiesByApartment(@PathVariable Integer id) {
        return amenityService.getAmenitiesByApartment(id);
    }

    @GetMapping("/types")
    public AmenityType[] getAmenityTypes() {
        return AmenityType.values();
    }
}