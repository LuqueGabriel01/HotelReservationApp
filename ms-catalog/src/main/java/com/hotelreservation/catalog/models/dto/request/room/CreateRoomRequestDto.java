package com.hotelreservation.catalog.models.dto.request.room;

import com.hotelreservation.catalog.enums.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Data transfer object representing the required request payload to create a new hotel room.
 * Includes data validation constraints for incoming parameters.
 */
public record CreateRoomRequestDto(
    @NotNull RoomType type,
    @Size(max = 150) String description,
    @Min(1) int capacity,
    @DecimalMin(value = "0.0", inclusive = false) BigDecimal pricePerNight) {}
