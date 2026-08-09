package com.dwellora.dto;

import java.time.LocalDateTime;

/** Data transfer object containing event response details. */
public record EventResponseDTO(
        Long eventId,
        Long apartmentId,
        String title,
        String description,
        LocalDateTime eventDate,
        Integer capacity,
        Integer currentRsvps,
        boolean isFull) {}