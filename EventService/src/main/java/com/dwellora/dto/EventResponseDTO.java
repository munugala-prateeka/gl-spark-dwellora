package com.dwellora.dto;

import java.time.LocalDateTime;

public record EventResponseDTO(
        Integer eventId,
        Integer apartmentId,
        String title,
        String description,
        LocalDateTime eventDate,
        Integer capacity,
        Integer currentRsvps,
        boolean isFull
) {}