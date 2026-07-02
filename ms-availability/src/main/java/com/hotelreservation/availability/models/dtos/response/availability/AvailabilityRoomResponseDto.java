package com.hotelreservation.availability.models.dtos.response.availability;

import com.hotelreservation.availability.enums.AvailabilityStatus;
import java.time.LocalDate;
import java.util.UUID;

/** Data transfer object (DTO) representing availability for a specific room between two dates. */
public record AvailabilityRoomResponseDto(
    UUID roomId,
    LocalDate checkIn,
    LocalDate checkOut,
    boolean available,
    AvailabilityStatus reason) {}
