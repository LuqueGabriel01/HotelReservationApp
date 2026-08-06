package com.hotelreservation.booking.models.dtos.event;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Event payload dispatched when a booking cancellation occurs. */
public record BookingCancelPayload(
    String eventType,
    UUID bookingId,
    UUID userId,
    String userEmail,
    String userName,
    String hotelName,
    String roomType,
    LocalDate checkIn,
    LocalDate checkOut,
    Instant timestamp) {
  /** Compact constructor to set a default timestamp if none is provided. */
  public BookingCancelPayload {
    if (timestamp == null) {
      timestamp = Instant.now();
    }
  }
}
