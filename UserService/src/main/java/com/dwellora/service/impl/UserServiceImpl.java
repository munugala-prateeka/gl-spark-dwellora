package com.dwellora.service.impl;

import com.dwellora.client.ApartmentClient;
import com.dwellora.dto.*;
import com.dwellora.entity.User;
import com.dwellora.enums.AccountStatus;
import com.dwellora.enums.Role;
import com.dwellora.exception.UserException;
import com.dwellora.repository.UserRepository;
import com.dwellora.service.UserService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ApartmentClient apartmentClient;

    public UserServiceImpl(UserRepository userRepository, ApartmentClient apartmentClient) {
        this.userRepository = userRepository;
        this.apartmentClient = apartmentClient;
    }

    @Override
    public ManagerResponseDTO createManager(UserRequestDTO request) {
        validateApartmentAndEmail(request.getApartmentId(), request.getEmail());

        if (userRepository.existsByApartmentIdAndRole(request.getApartmentId(), Role.MANAGER)) {
            throw new UserException("Manager already exists for this apartment");
        }

        User user = mapToEntity(request);
        user.setRole(Role.MANAGER);
        user.setAccountStatus(AccountStatus.ACTIVE);

        User saved = userRepository.save(user);

        return new ManagerResponseDTO(
                saved.getUserId(),
                saved.getApartmentId(),
                saved.getFullName(),
                saved.getRole().name(),
                saved.getEmail());
    }


    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public UserResponseDTO getUserById(Integer userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserException("User not found with id: " + userId));
        return mapToResponse(user);
    }

    private void validateApartmentAndEmail(Integer apartmentId, String email) {
        try {
            Object apartment = apartmentClient.getApartmentById(apartmentId);
            if (apartment == null) {
                throw new UserException("Apartment not found with id: " + apartmentId);
            }
        } catch (Exception ex) {
            throw new UserException("Apartment not found or Apartment Service unavailable");
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserException("Email already exists");
        }
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new UserException("Invalid email or password."));

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new UserException("Account is inactive.");
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new UserException("Invalid email or password.");
        }

        return new LoginResponseDTO(
                user.getUserId(),
                user.getApartmentId(),
                user.getFullName(),
                user.getRole().name(),
                user.getEmail());
    }

    private User mapToEntity(UserRequestDTO request) {
        User user = new User();
        user.setApartmentId(request.getApartmentId());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setFlatNumber(request.getFlatNumber());
        return user;
    }

    private UserResponseDTO mapToResponse(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getApartmentId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getFlatNumber(),
                user.getRole(),
                user.getAccountStatus());
    }
}