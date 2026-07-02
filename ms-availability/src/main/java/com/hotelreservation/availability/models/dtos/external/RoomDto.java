package com.hotelreservation.availability.models.dtos.external;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data transfer object representing the complete profile and availability metadata of a hotel room.
 */
public record RoomDto(
    UUID id,
    UUID hotelId,
    String type,
    String description,
    int capacity,
    BigDecimal pricePerNight) {}
