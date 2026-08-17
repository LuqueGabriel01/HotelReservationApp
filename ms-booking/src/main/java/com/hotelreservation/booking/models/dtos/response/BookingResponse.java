package com.hotelreservation.booking.models.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Standard response payload representing a confirmed or processed booking. */
public record BookingResponse(
    UUID id,
    UUID userId,
    UUID hotelId,
    String hotelName,
    UUID roomId,
    String roomType,
    LocalDate checkIn,
    LocalDate checkOut,
    Integer nights,
    BigDecimal pricePerNight,
    BigDecimal totalPrice,
    String status,
    Instant createdAt) {}
