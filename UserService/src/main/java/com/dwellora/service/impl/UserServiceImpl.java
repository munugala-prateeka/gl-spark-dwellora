package com.dwellora.service.impl;

import com.dwellora.dto.ActivateAccountDTO;
import com.dwellora.dto.LoginRequestDTO;
import com.dwellora.dto.LoginResponseDTO;
import com.dwellora.dto.ResidentRequestDTO;
import com.dwellora.dto.UserResponseDTO;
import com.dwellora.dto.UserUpdateRequestDTO;
import com.dwellora.entity.User;
import com.dwellora.enums.AccountStatus;
import com.dwellora.enums.Role;
import com.dwellora.event.ResidentCreatedEvent;
import com.dwellora.exception.UserException;
import com.dwellora.kafka.ResidentProducer;
import com.dwellora.repository.UserRepository;
import com.dwellora.security.JwtUtil;
import com.dwellora.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing user authentication, resident onboarding, and user account operations.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ResidentProducer residentProducer;

    public UserServiceImpl(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            ResidentProducer residentProducer) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.residentProducer = residentProducer;
    }

    /**
     * Authenticates a user with email and password, returning a JWT token upon success.
     */

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        String email = normalizeEmail(request.getEmail());

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UserException("Invalid email or password."));

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new UserException("Account is inactive.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException("Invalid email or password.");
        }

        String token =
                jwtUtil.generateToken(
                        user.getUserId(), user.getEmail(), user.getRole().name(), user.getApartmentId());

        LoginResponseDTO response =
                new LoginResponseDTO(
                        user.getUserId(),
                        user.getApartmentId(),
                        user.getFullName(),
                        user.getRole().name(),
                        user.getEmail());
        response.setToken(token);
        return response;
    }

    /**
     * Retrieves a list of all registered users.
     */
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    /**
     * Retrieves a user by their unique identifier.
     */
    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserException("User not found with id: " + userId));
        return mapToResponse(user);
    }

    /**
     * Validates that the provided email address is not already tied to an active user account.
     */
    private void validateEmail(String email) {

        String normalizedEmail = normalizeEmail(email);
        if (userRepository
                .findByEmailAndAccountStatusNot(normalizedEmail, AccountStatus.INACTIVE)
                .isPresent()) {
            throw new UserException("Email already exists");
        }
    }

    /**
     * Activates a pending user account using a valid activation token and updates the user password.
     */
    @Override
    public LoginResponseDTO activateAccount(ActivateAccountDTO dto) {
        User user =
                userRepository
                        .findByActivationToken(dto.getToken())
                        .orElseThrow(() -> new UserException("Invalid activation token."));

        if (user.getAccountStatus() != AccountStatus.PENDING_ACTIVATION) {
            throw new UserException("Account is already activated.");
        }

        if (user.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new UserException("Activation token has expired.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setActivationToken(null);
        user.setActivationTokenExpiry(null);

        User saved = userRepository.save(user);

        return new LoginResponseDTO(
                saved.getUserId(),
                saved.getApartmentId(),
                saved.getFullName(),
                saved.getRole().name(),
                saved.getEmail());
    }

    /**
     * Creates a new resident for an apartment and publishes a {@link ResidentCreatedEvent}.
     */
    @Override
    public UserResponseDTO createResident(Long apartmentId, ResidentRequestDTO request) {
        validateEmail(request.getEmail());

        User user = mapResidentToEntity(apartmentId, request);
        user.setRole(Role.RESIDENT);
        user.setAccountStatus(AccountStatus.PENDING_ACTIVATION);
        user.setPassword(null);

        String token = UUID.randomUUID().toString();
        user.setActivationToken(token);
        user.setActivationTokenExpiry(LocalDateTime.now().plusHours(24));

        User saved = userRepository.save(user);

        ResidentCreatedEvent event = new ResidentCreatedEvent();
        event.setUserId(saved.getUserId());
        event.setApartmentId(saved.getApartmentId());
        event.setResidentName(saved.getFullName());
        event.setResidentEmail(saved.getEmail());
        event.setActivationToken(token);
        residentProducer.publish(event);

        return mapToResponse(saved);
    }

    /**
     * Retrieves all active residents associated with a specific apartment.
     */
    @Override
    public List<UserResponseDTO> getResidentsByApartment(Long apartmentId) {
        return userRepository.findByApartmentIdAndRole(apartmentId, Role.RESIDENT).stream()
                .filter(user -> user.getAccountStatus() != AccountStatus.INACTIVE)
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Updates existing resident profile details and optionally resets their password.
     */
    @Override
    public UserResponseDTO updateResident(
            Long apartmentId, Long userId, UserUpdateRequestDTO request) {
        User existing =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserException("User not found with id: " + userId));

        if (!existing.getApartmentId().equals(apartmentId)) {
            throw new UserException("Resident not found in your apartment.");
        }

        existing.setFullName(request.getFullName());
        existing.setEmail(normalizeEmail(request.getEmail()));
        existing.setPhone(request.getPhone());
        existing.setFlatNumber(request.getFlatNumber());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(existing);
        return mapToResponse(updated);
    }

    /**
     * Soft-deletes a resident from an apartment by transitioning their account status to INACTIVE.
     */
    @Override
    public void deleteResident(Long apartmentId, Long userId) {
        User existing =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserException("User not found with id: " + userId));

        if (existing.getRole() == Role.MANAGER) {
            throw new UserException("Manager cannot be deleted.");
        }

        if (!existing.getApartmentId().equals(apartmentId)) {
            throw new UserException("Resident not found in your apartment.");
        }

        existing.setAccountStatus(AccountStatus.INACTIVE);
        userRepository.save(existing);
    }

    /**
     * Maps a {@link User} entity to its corresponding response DTO.
     */
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

    /**
     * Maps a {@link ResidentRequestDTO} into a new {@link User} entity instance.
     */
    private User mapResidentToEntity(Long apartmentId, ResidentRequestDTO request) {
        User user = new User();
        user.setApartmentId(apartmentId);
        user.setFullName(request.getFullName());
        user.setEmail(normalizeEmail(request.getEmail()));
        user.setPhone(request.getPhone());
        user.setFlatNumber(request.getFlatNumber());
        return user;
    }
}