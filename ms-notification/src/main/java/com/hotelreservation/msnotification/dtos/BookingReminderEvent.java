package com.hotelreservation.msnotification.dtos;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Event received 24 hours before check-in to send a reminder. Posted by ms-booking in the
 * ‘booking.reminder’ thread
 */
public record BookingReminderEvent(
    String eventType,
    UUID bookingId,
    UUID userId,
    String userEmail,
    String username,
    String hotelName,
    String hotelAddress,
    String roomName,
    LocalDate checkIn,
    LocalDate checkOut,
    Instant timestamp) {}
