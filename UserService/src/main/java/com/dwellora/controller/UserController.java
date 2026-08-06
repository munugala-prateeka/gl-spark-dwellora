package com.dwellora.controller;

import com.dwellora.dto.*;
import com.dwellora.service.UserService;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/managers")
    public ResponseEntity<ManagerResponseDTO> createManager(@Valid @RequestBody UserRequestDTO request) {
        ManagerResponseDTO response = userService.createManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/activate")
    public ResponseEntity<LoginResponseDTO> activateAccount(@Valid @RequestBody ActivateAccountDTO dto) {
        return ResponseEntity.ok(userService.activateAccount(dto));
    }

    @PostMapping("/residents")
    public ResponseEntity<UserResponseDTO> createResident(@Valid @RequestBody ResidentRequestDTO request) {
        UserResponseDTO response = userService.createResident(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/apartment/{apartmentId}/residents")
    public ResponseEntity<List<UserResponseDTO>> getResidentsByApartment(@PathVariable Integer apartmentId) {
        return ResponseEntity.ok(userService.getResidentsByApartment(apartmentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateResident(
            @PathVariable Integer id, @Valid @RequestBody UserUpdateRequestDTO request) {
        return ResponseEntity.ok(userService.updateResident(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResident(@PathVariable Integer id) {
        userService.deleteResident(id);
        return ResponseEntity.noContent().build();
    }
}