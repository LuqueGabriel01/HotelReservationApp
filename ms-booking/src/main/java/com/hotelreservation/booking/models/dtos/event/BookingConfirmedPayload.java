package com.hotelreservation.booking.models.dtos.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Event payload dispatched when a booking is successfully confirmed. */
public record BookingConfirmedPayload(
    String eventType,
    UUID bookingId,
    UUID userId,
    String userEmail,
    String username,
    UUID hotelId,
    String hotelName,
    String roomType,
    LocalDate checkIn,
    LocalDate checkOut,
    BigDecimal totalPrice,
    Instant timestamp) {
  /** Compact constructor to set a default timestamp if none is provided. */
  public BookingConfirmedPayload {
    if (timestamp == null) {
      timestamp = Instant.now();
    }
  }
}
