package com.dwellora.client;

import com.dwellora.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the USER-SERVICE microservice.
 */
@FeignClient(name = "USER-SERVICE", fallback = UserClientFallback.class)
public interface UserClient {

    /**
     * Retrieves user details by their unique identifier.
     */
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}