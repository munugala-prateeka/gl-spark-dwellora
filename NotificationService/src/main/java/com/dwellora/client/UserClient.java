package com.dwellora.client;

import com.dwellora.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/users/apartment/{apartmentId}/residents")
    List<UserResponseDTO> getResidentsByApartment(@PathVariable("apartmentId") Integer apartmentId);
}