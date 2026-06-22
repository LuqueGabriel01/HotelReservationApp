package com.hotelreservation.catalog.models.dto.request.room;

import com.hotelreservation.catalog.enums.RoomType;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Data transfer object representing the request payload to update an existing hotel room record.
 * All fields are optional to allow partial updates while maintaining structural validation limits.
 */
public record UpdateRoomRequestDto(
    RoomType type,
    @Size(max = 150) String description,
    Integer capacity,
    BigDecimal pricePerNight) {}
