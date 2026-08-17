package com.hotelreservation.booking.models.dtos.response;

import java.time.Instant;
import lombok.Builder;

/** Standardized response payload representing an application error or exception. */
@Builder
public record ErrorResponse(int code, String name, String description, Instant timestamp) {}
