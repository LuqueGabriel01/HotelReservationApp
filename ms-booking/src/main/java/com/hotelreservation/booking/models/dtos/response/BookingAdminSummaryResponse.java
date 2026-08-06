package com.hotelreservation.booking.models.dtos.response;

import com.hotelreservation.booking.models.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Administrative summary response containing high-level booking details. */
public record BookingAdminSummaryResponse(
    UUID id,
    UUID userId,
    String hotelName,
    String roomType,
    LocalDate checkIn,
    LocalDate checkOut,
    BigDecimal totalPrice,
    BookingStatus status) {}
