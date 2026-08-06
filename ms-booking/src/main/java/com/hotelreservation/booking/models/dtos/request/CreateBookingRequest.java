package com.hotelreservation.booking.models.dtos.request;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

/** Request for creating a new booking reservation. */
@Builder
public record CreateBookingRequest(
    UUID roomId, UUID hotelId, LocalDate checkIn, LocalDate checkOut, UUID lockId) {}
