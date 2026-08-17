package com.hotelreservation.booking.models.dtos.internal;

import java.util.UUID;

/**
 * Internal representation of a room lock release operation result from the availability service.
 */
public record InternalAvailabilityRelease(boolean released, UUID roomId, UUID bookingId) {}
