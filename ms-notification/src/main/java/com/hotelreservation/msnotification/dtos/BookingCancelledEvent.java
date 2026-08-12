package com.hotelreservation.msnotification.dtos;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Event triggered when a booking has been cancelled. Posted by ms-booking in the
 * ‘booking.cancelled’ thread.
 */
public record BookingCancelledEvent(
    String eventType,
    UUID bookingId,
    UUID userId,
    String userEmail,
    String username,
    String hotelName,
    String roomName,
    LocalDate checkIn,
    LocalDate checkOut,
    Instant timestamp) {}
