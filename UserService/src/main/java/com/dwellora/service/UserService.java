package com.dwellora.service;

import com.dwellora.dto.ManagerResponseDTO;
import com.dwellora.dto.UserRequestDTO;
import com.dwellora.dto.UserResponseDTO;
import java.util.List;

public interface UserService {

    ManagerResponseDTO createManager(UserRequestDTO request);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Integer userId);

}
