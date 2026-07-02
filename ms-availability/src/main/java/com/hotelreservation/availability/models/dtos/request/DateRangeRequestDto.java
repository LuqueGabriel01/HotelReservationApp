package com.hotelreservation.availability.models.dtos.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Data transfer object (DTO) representing date range request for room blocks. */
public record DateRangeRequestDto(
    @NotNull() @Future LocalDate checkIn, @NotNull() @Future LocalDate checkOut) {}
