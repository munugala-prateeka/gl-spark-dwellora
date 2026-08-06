package com.dwellora.controller;

import com.dwellora.dto.ApartmentRequestDTO;
import com.dwellora.dto.ApartmentResponseDTO;
import com.dwellora.service.ApartmentService;
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
@RequestMapping("/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @PostMapping
    public ResponseEntity<ApartmentResponseDTO> addApartment(@Valid @RequestBody ApartmentRequestDTO dto) {
        ApartmentResponseDTO response = apartmentService.addApartment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ApartmentResponseDTO>> getAllApartments() {
        return ResponseEntity.ok(apartmentService.getAllApartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartmentResponseDTO> getApartment(@PathVariable Integer id) {
        return ResponseEntity.ok(apartmentService.getApartmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApartmentResponseDTO> updateApartment(
            @PathVariable Integer id, @Valid @RequestBody ApartmentRequestDTO dto) {
        return ResponseEntity.ok(apartmentService.updateApartment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApartment(@PathVariable Integer id) {
        apartmentService.deleteApartment(id);
        return ResponseEntity.noContent().build();
    }
}