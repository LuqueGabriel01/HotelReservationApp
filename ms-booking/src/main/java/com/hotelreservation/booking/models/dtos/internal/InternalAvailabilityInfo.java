package com.hotelreservation.booking.models.dtos.internal;

import java.util.UUID;

/** Internal representation of room availability details retrieved from the availability service. */
public record InternalAvailabilityInfo(UUID roomId, UUID bookingId, String status) {}
