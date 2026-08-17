package com.hotelreservation.availability.models.dtos.response.availability;

import java.math.BigDecimal;
import java.util.UUID;

/** Data transfer object representing available rooms from a specific hotel between two dates. */
public record AvailabilityResponseDto(
    UUID roomId, String type, int capacity, BigDecimal pricePerNight, boolean available) {}
