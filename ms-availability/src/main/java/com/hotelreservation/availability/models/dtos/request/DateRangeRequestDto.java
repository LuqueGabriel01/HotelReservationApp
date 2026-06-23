package com.hotelreservation.availability.models.dtos.request;

import java.time.LocalDate;

/** Data transfer object (DTO) representing date range request for room blocks. */
public record DateRangeRequestDto(
        LocalDate checkIn,
        LocalDate checkOut
) {
}
