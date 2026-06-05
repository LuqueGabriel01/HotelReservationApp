package com.hotelreservation.auth.models.dtos.response;

import com.hotelreservation.auth.enums.Role;

import java.time.Instant;
import java.util.UUID;

/** Data transfer object (DTO) representing the public profile and metadata of a user account. */
public record UserResponse(
        UUID id,
        String username,
        String email,
        Role role,
        Instant createdAt,
        Instant updatedAt
) {}
