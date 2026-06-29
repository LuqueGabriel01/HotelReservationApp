package com.hotelreservation.availability.models.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;
/** Data transfer object (DTO) representing request for reserving the block room. */
public record ReserveRoomRequestDto(
        @NotNull
        UUID lockId,

        @NotNull
        UUID bookingId
) {
}
