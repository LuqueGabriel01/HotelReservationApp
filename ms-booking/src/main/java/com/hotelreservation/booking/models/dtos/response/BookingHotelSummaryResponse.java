package com.hotelreservation.booking.models.dtos.response;

import com.hotelreservation.booking.models.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Hotel-centric summary response containing key reservation details. */
public record BookingHotelSummaryResponse(
    UUID id,
    UUID userId,
    String roomType,
    LocalDate checkIn,
    LocalDate checkOut,
    BigDecimal totalPrice,
    BookingStatus status) {}
