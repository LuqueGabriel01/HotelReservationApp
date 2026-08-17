package com.hotelreservation.catalog.models.dto.external;

import com.hotelreservation.catalog.enums.RoomType;
import java.math.BigDecimal;
import java.util.UUID;

/** Data transfer object representing the data from hotel and room that booking needs. */
public record BookingRequestDto(
    UUID id,
    UUID hotelId,
    String hotelName,
    String hotelAddress,
    RoomType type,
    String description,
    int capacity,
    BigDecimal pricePerNight) {}
