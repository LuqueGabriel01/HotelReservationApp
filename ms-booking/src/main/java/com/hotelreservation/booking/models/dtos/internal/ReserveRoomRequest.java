package com.hotelreservation.booking.models.dtos.internal;

import java.util.UUID;

/** Request payload for finalizing a room reservation using a lock identifier. */
public record ReserveRoomRequest(UUID lockId, UUID bookingId) {}
