package com.dwellora.service.impl;

import com.dwellora.client.ApartmentClient;
import com.dwellora.dto.*;
import com.dwellora.entity.User;
import com.dwellora.enums.AccountStatus;
import com.dwellora.enums.Role;
import com.dwellora.exception.UserException;
import com.dwellora.repository.UserRepository;
import com.dwellora.security.JwtUtil;
import com.dwellora.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.dwellora.event.ResidentCreatedEvent;
import com.dwellora.kafka.ResidentProducer;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ApartmentClient apartmentClient;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ResidentProducer residentProducer;

    public UserServiceImpl(UserRepository userRepository,
                           ApartmentClient apartmentClient,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder,
                           ResidentProducer residentProducer) {
        this.userRepository = userRepository;
        this.apartmentClient = apartmentClient;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.residentProducer = residentProducer;
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
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);

        return new ManagerResponseDTO(
                saved.getUserId(),
                saved.getApartmentId(),
                saved.getFullName(),
                saved.getRole().name(),
                saved.getEmail());
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException("Invalid email or password."));

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new UserException("Account is inactive.");
        }

        // Verify encrypted password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRole().name());

        LoginResponseDTO response = new LoginResponseDTO(
                user.getUserId(), user.getApartmentId(), user.getFullName(),
                user.getRole().name(), user.getEmail());
        response.setToken(token);
        return response;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public UserResponseDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
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

        if (userRepository.findByEmailAndAccountStatusNot(email, AccountStatus.INACTIVE).isPresent()) {
            throw new UserException("Email already exists");
        }
    }

    @Override
    public LoginResponseDTO activateAccount(ActivateAccountDTO dto) {
        User user = userRepository.findByActivationToken(dto.getToken())
                .orElseThrow(() -> new UserException("Invalid activation token."));

        if (user.getAccountStatus() != AccountStatus.PENDING_ACTIVATION) {
            throw new UserException("Account is already activated.");
        }

        if (user.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new UserException("Activation token has expired.");
        }

        // in activateAccount()
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

    @Override
    public UserResponseDTO createResident(ResidentRequestDTO request) {
        validateApartmentAndEmail(request.getApartmentId(), request.getEmail());

        User user = mapResidentToEntity(request);
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

    @Override
    public List<UserResponseDTO> getResidentsByApartment(Integer apartmentId) {
        return userRepository.findByApartmentIdAndRole(apartmentId, Role.RESIDENT).stream()
                .filter(user -> user.getAccountStatus() != AccountStatus.INACTIVE) // Filter out deleted/inactive
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponseDTO updateResident(Integer userId, UserUpdateRequestDTO request) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found with id: " + userId));

        existing.setFullName(request.getFullName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setFlatNumber(request.getFlatNumber());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void deleteResident(Integer userId) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found with id: " + userId));

        if (existing.getRole() == Role.MANAGER) {
            throw new UserException("Manager cannot be deleted.");
        }

        // Soft Delete
        existing.setAccountStatus(AccountStatus.INACTIVE);
        userRepository.save(existing);
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

    private User mapResidentToEntity(ResidentRequestDTO request) {
        User user = new User();
        user.setApartmentId(request.getApartmentId());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setFlatNumber(request.getFlatNumber());
        return user;
    }
}