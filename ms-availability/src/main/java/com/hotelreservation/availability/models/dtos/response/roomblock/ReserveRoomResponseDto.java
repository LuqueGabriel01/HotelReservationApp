package com.hotelreservation.availability.models.dtos.response.roomblock;

import com.hotelreservation.availability.enums.AvailabilityStatus;

import java.util.UUID;

/** Data transfer object (DTO) representing permanent blocked rooms. */
public record ReserveRoomResponseDto(
        UUID roomId,
        UUID bookingId,
        AvailabilityStatus status
) {
}
