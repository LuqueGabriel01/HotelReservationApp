package com.hotelreservation.availability.models.dtos.response;

import java.time.Instant;
import lombok.Builder;

/** Data transfer object (DTO) representing an API error response. */
@Builder
public record ErrorResponse(int code, String name, String description, Instant timestamp) {}
