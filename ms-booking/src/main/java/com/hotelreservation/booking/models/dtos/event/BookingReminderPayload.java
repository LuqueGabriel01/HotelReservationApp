package com.hotelreservation.booking.models.dtos.event;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Event payload containing booking reminder details for notification processing. */
public record BookingReminderPayload(
    String eventType,
    UUID bookingId,
    UUID userId,
    String userEmail,
    String username,
    String hotelName,
    String hotelAddress,
    String roomType,
    LocalDate checkIn,
    LocalDate checkOut,
    Instant timestamp) {

  /** Compact constructor that assigns a default timestamp if omitted. */
  public BookingReminderPayload {
    if (timestamp == null) {
      timestamp = Instant.now();
    }
  }
}
