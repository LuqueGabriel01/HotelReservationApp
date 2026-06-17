package com.hotelreservation.catalog.models.dto.response;

import lombok.Builder;

import java.time.Instant;

/** Data transfer object (DTO) representing an API error response.*/
@Builder
public record ErrorResponse(
        int code,
        String name,
        String description,
        Instant timestamp
) {}