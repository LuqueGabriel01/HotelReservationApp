package com.hotelreservation.msagent.dto;

import java.time.Instant;

/** Data transfer object (DTO) representing an API error response. */
public record ErrorResponse(Integer code, String name, String description, Instant timestamp) {}
