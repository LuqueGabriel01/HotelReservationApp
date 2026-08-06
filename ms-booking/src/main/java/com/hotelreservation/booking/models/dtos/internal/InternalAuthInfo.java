package com.hotelreservation.booking.models.dtos.internal;

import java.util.UUID;

/** Internal representation of user authentication details retrieved from the auth service. */
public record InternalAuthInfo(UUID id, String username, String email, String role) {}
