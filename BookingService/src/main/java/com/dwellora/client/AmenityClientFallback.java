package com.dwellora.client;

import com.dwellora.dto.AmenityDTO;
import org.springframework.stereotype.Component;

@Component
public class AmenityClientFallback implements AmenityClient {

    @Override
    public AmenityDTO getAmenityById(Long id) {
        return null;
    }
}