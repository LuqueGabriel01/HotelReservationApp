package com.hotelreservation.availability.models.dtos.request;

import java.util.UUID;
/** Data transfer object (DTO) representing request for reserving the block room. */
public record ReserveRoomRequestDto(
        UUID lockId,
        UUID bookingId
) {
}
