package com.dwellora.service;

import com.dwellora.dto.*;

import java.util.List;

public interface UserService {

    ManagerResponseDTO createManager(UserRequestDTO request);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Integer userId);

    LoginResponseDTO login(LoginRequestDTO request);

    LoginResponseDTO activateAccount(ActivateAccountDTO dto);

    UserResponseDTO createResident(ResidentRequestDTO request);

    List<UserResponseDTO> getResidentsByApartment(Integer apartmentId);

    UserResponseDTO updateResident(Integer userId, UserUpdateRequestDTO request);

    void deleteResident(Integer userId);
}
