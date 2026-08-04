package com.dwellora.controller;

import com.dwellora.dto.*;
import com.dwellora.service.UserService;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/managers")
    public ManagerResponseDTO createManager(@Valid @RequestBody UserRequestDTO request) {
        return userService.createManager(request);
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return userService.login(request);
    }

    @PostMapping("/activate")
    public LoginResponseDTO activateAccount(@Valid @RequestBody ActivateAccountDTO dto) {
        return userService.activateAccount(dto);
    }

    @PostMapping("/residents")
    public UserResponseDTO createResident(@Valid @RequestBody ResidentRequestDTO request) {
        return userService.createResident(request);
    }

    @GetMapping("/apartment/{apartmentId}/residents")
    public List<UserResponseDTO> getResidentsByApartment(@PathVariable Integer apartmentId) {
        return userService.getResidentsByApartment(apartmentId);
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateResident(@PathVariable Integer id, @Valid @RequestBody UserUpdateRequestDTO request) {
        return userService.updateResident(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteResident(@PathVariable Integer id) {
        userService.deleteResident(id);
    }
}