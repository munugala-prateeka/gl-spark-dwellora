package com.dwellora.client;

import com.dwellora.dto.AmenityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the AMENITY-SERVICE microservice.
 */
@FeignClient(name = "AMENITY-SERVICE", fallback = AmenityClientFallback.class)
public interface AmenityClient {

    /**
     * Retrieves amenity details by its unique identifier.
     */
    @GetMapping("/amenities/{id}")
    AmenityDTO getAmenityById(@PathVariable Long id);
}