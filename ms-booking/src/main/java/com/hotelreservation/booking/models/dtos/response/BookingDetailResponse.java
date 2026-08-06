package com.hotelreservation.booking.models.dtos.response;

import com.hotelreservation.booking.models.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Detailed response containing comprehensive information about a booking, including status history.
 */
public record BookingDetailResponse(
    UUID id,
    String hotelName,
    String roomType,
    LocalDate checkIn,
    LocalDate checkOut,
    Integer nights,
    BigDecimal pricePerNight,
    BigDecimal totalPrice,
    BookingStatus status,
    List<BookingStatusHistoryResponse> history) {}
