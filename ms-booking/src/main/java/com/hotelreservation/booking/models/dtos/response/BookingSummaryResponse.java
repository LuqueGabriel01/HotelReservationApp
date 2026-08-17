package com.hotelreservation.booking.models.dtos.response;

import com.hotelreservation.booking.models.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Lightweight summary response containing essential booking details for end users. */
public record BookingSummaryResponse(
    UUID id,
    String hotelName,
    String roomType,
    LocalDate checkIn,
    LocalDate checkOut,
    BigDecimal totalPrice,
    BookingStatus status) {}
