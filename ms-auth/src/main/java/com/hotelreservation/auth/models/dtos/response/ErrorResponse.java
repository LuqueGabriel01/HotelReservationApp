package com.hotelreservation.auth.models.dtos.response;

import lombok.Builder;

import java.time.Instant;

/** data transfer object (DTO) representing an API error response. */
@Builder
public record ErrorResponse(
        int code,
        String name,
        String description,
        Instant timestamp
) {}
