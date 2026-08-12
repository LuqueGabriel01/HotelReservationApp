package com.hotelreservation.msnotification.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * An event triggered when a booking has been confirmed. Published by ms-booking in the
 * 'booking.confirmed' topic.
 */
public record BookingConfirmedEvent(
    String eventType,
    UUID bookingId,
    UUID userId,
    String userEmail,
    String username,
    UUID hotelId,
    String hotelName,
    String roomName,
    LocalDate checkIn,
    LocalDate checkOut,
    BigDecimal totalPrice,
    Instant timestamp) {}
