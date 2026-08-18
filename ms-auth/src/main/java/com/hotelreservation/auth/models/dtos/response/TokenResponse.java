package com.hotelreservation.auth.models.dtos.response;

import lombok.Builder;

/**
 * Data transfer object representing the authenticated user's information extracted from a JWT
 * token.
 */
@Builder
public record TokenResponse(
    String accessToken, String refreshToken, String tokenType, int expiresIn) {}
