package com.hotelreservation.booking.models.dtos.internal;

import java.util.UUID;

/** Request payload for releasing a room lock associated with a booking. */
public record ReleaseRoomRequest(UUID bookingId) {}
