package com.dwellora.client;

import com.dwellora.dto.UserResponseDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for communicating with the USER-SERVICE microservice.
 */
@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/users/residents")
    List<UserResponseDTO> getResidentsByApartment(
            @RequestHeader("X-Apartment-Id") Long apartmentId);
}