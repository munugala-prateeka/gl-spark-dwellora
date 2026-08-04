package com.dwellora.service;

import com.dwellora.dto.*;

import java.util.List;

public interface UserService {

    ManagerResponseDTO createManager(UserRequestDTO request);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Integer userId);

    LoginResponseDTO login(LoginRequestDTO request);

    LoginResponseDTO activateAccount(ActivateAccountDTO dto);
}