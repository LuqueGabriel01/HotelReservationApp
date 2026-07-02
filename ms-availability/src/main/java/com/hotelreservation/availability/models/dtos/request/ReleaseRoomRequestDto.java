package com.hotelreservation.availability.models.dtos.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Data transfer object (DTO) representing room release request. */
public record ReleaseRoomRequestDto(@NotNull UUID bookingId) {}
