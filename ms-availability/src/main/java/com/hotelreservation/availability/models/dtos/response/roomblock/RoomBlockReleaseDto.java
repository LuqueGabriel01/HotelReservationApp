package com.hotelreservation.availability.models.dtos.response.roomblock;

import java.util.UUID;

/** Data transfer object (DTO) representing room release response. */
public record RoomBlockReleaseDto(
        boolean released,
        UUID roomId,
        UUID bookingId
) {
}
