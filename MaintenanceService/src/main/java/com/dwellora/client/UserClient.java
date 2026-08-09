package com.dwellora.client;

import com.dwellora.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** REST client for communicating with the user management microservice. */
@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    /** Fetches user details by user ID from the user service. */
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}