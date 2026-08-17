package com.hotelreservation.booking.models.dtos.response;

import com.hotelreservation.booking.models.enums.BookingStatus;
import java.time.Instant;

/** Response payload representing an entry in a booking's status transition history. */
public record BookingStatusHistoryResponse(
    BookingStatus previousStatus, BookingStatus newStatus, Instant createdAt, String reason) {}
