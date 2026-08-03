package com.dwellora.controller;

import com.dwellora.dto.ApartmentRequestDTO;
import com.dwellora.dto.ApartmentResponseDTO;
import com.dwellora.service.ApartmentService;
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
@RequestMapping("/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @PostMapping
    public ApartmentResponseDTO addApartment(@Valid @RequestBody ApartmentRequestDTO dto) {
        return apartmentService.addApartment(dto);
    }

    @GetMapping
    public List<ApartmentResponseDTO> getAllApartments() {
        return apartmentService.getAllApartments();
    }

    @GetMapping("/{id}")
    public ApartmentResponseDTO getApartment(@PathVariable Integer id) {
        return apartmentService.getApartmentById(id);
    }

    @PutMapping("/{id}")
    public ApartmentResponseDTO updateApartment(
            @PathVariable Integer id, @Valid @RequestBody ApartmentRequestDTO dto) {
        return apartmentService.updateApartment(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteApartment(@PathVariable Integer id) {
        apartmentService.deleteApartment(id);
    }
}