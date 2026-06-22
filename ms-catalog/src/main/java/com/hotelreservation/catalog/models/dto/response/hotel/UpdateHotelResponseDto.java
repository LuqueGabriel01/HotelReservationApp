package com.hotelreservation.catalog.models.dto.response.hotel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data transfer object representing the confirmation response metadata after successfully updating
 * a hotel.
 */
public record UpdateHotelResponseDto(
    UUID id,
    String name,
    String description,
    String address,
    String city,
    int stars,
    List<String> amenities,
    LocalDateTime updatedAt) {}
