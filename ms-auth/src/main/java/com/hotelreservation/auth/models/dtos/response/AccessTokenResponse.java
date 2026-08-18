package com.hotelreservation.auth.models.dtos.response;

import lombok.Builder;

/**
 * Data transfer object (DTO) exposing only the access token and its session metadata.
 *
 * <p>Used wherever a session's tokens are embedded in a client-facing response (e.g. {@link
 * RegisterResponse}) without leaking the refresh token, which travels only as an {@code HttpOnly}
 * cookie.
 */
@Builder
public record AccessTokenResponse(String accessToken, String tokenType, int expiresIn) {}
