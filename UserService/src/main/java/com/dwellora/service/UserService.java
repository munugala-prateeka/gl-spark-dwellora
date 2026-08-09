package com.dwellora.service;

import com.dwellora.dto.ActivateAccountDTO;
import com.dwellora.dto.LoginRequestDTO;
import com.dwellora.dto.LoginResponseDTO;
import com.dwellora.dto.ResidentRequestDTO;
import com.dwellora.dto.UserResponseDTO;
import com.dwellora.dto.UserUpdateRequestDTO;
import java.util.List;

/**
 * Service interface defining core operations for user management, authentication, and resident onboarding.
 */
public interface UserService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long userId);

    LoginResponseDTO login(LoginRequestDTO request);

    LoginResponseDTO activateAccount(ActivateAccountDTO dto);

    UserResponseDTO createResident(Long apartmentId, ResidentRequestDTO request);

    List<UserResponseDTO> getResidentsByApartment(Long apartmentId);

    UserResponseDTO updateResident(Long apartmentId, Long userId, UserUpdateRequestDTO request);

    void deleteResident(Long apartmentId, Long userId);
}