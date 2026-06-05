package com.hotelreservation.auth.models.dtos.response;

import lombok.Builder;

/** Data transfer object (DTO) containing the authentication tokens and session duration details. */
@Builder
public record LoginResponse(
   String accessToken,
   String refreshToken,
   String tokenType,
   int expiresIn
) {}
