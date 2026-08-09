package com.dwellora.controller;

import com.dwellora.dto.ActivateAccountDTO;
import com.dwellora.dto.LoginRequestDTO;
import com.dwellora.dto.LoginResponseDTO;
import com.dwellora.dto.ResidentRequestDTO;
import com.dwellora.dto.UserResponseDTO;
import com.dwellora.dto.UserUpdateRequestDTO;
import com.dwellora.service.UserService;
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

/**
 * REST controller for managing users and residents.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users.
     */
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Retrieves a user by their ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Authenticates a user and returns a login response.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(userService.login(request));
    }

    /**
     * Activates a user account.
     */
    @PostMapping("/activate")
    public ResponseEntity<LoginResponseDTO> activateAccount(
            @Valid @RequestBody ActivateAccountDTO dto) {
        return ResponseEntity.ok(userService.activateAccount(dto));
    }

    /**
     * Creates a new resident for a specific apartment.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/residents")
    public ResponseEntity<UserResponseDTO> createResident(
            @RequestHeader("X-Apartment-Id") Long apartmentId,
            @Valid @RequestBody ResidentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createResident(apartmentId, request));
    }

    /**
     * Retrieves all residents for a specific apartment.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/residents")
    public ResponseEntity<List<UserResponseDTO>> getResidentsByApartment(
            @RequestHeader("X-Apartment-Id") Long apartmentId) {
        return ResponseEntity.ok(userService.getResidentsByApartment(apartmentId));
    }

    /**
     * Updates a resident's details for a specific apartment.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateResident(
            @RequestHeader("X-Apartment-Id") Long apartmentId,
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO request) {
        return ResponseEntity.ok(userService.updateResident(apartmentId, id, request));
    }

    /**
     * Deletes a resident by ID for a specific apartment.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResident(
            @RequestHeader("X-Apartment-Id") Long apartmentId, @PathVariable Long id) {
        userService.deleteResident(apartmentId, id);
        return ResponseEntity.noContent().build();
    }
}