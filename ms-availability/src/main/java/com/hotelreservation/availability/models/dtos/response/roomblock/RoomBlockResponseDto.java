package com.hotelreservation.availability.models.dtos.response.roomblock;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Data transfer object (DTO) representing blocked room response. */
public record RoomBlockResponseDto(
    UUID lockId, UUID roomId, LocalDate checkIn, LocalDate checkOut, Instant expiresAt) {}
