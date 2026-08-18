package com.hotelreservation.auth.models.dtos.response;

import com.hotelreservation.auth.enums.Role;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

/**
 * Data transfer object (DTO) containing the newly created user profile and initial session tokens.
 */
@Builder
public record RegisterResponse(
    UUID id,
    String username,
    String email,
    Role role,
    AccessTokenResponse tokens,
    Instant createdAt) {}
