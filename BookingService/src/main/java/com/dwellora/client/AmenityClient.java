package com.dwellora.client;

import com.dwellora.dto.AmenityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "AMENITY-SERVICE")
public interface AmenityClient {

    @GetMapping("/amenities/{id}")
    AmenityDTO getAmenityById(@PathVariable Integer id);

}