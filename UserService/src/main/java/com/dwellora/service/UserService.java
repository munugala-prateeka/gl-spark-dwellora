package com.dwellora.service;

import com.dwellora.dto.LoginRequestDTO;
import com.dwellora.dto.LoginResponseDTO;
import com.dwellora.dto.ManagerResponseDTO;
import com.dwellora.dto.UserRequestDTO;
import com.dwellora.dto.UserResponseDTO;
import com.dwellora.dto.UserUpdateRequestDTO;
import java.util.List;

public interface UserService {

    ManagerResponseDTO createManager(UserRequestDTO request);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Integer userId);

}