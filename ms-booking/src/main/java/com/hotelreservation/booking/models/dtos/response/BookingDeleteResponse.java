package com.hotelreservation.booking.models.dtos.response;

import com.hotelreservation.booking.models.enums.BookingStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

/** Response payload returned after successfully deleting or cancelling a booking. */
@Builder
public record BookingDeleteResponse(UUID id, BookingStatus status, Instant createAt) {}
