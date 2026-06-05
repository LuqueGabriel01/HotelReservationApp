package com.hotelreservation.auth.models.dtos.response;

/** Data transfer object (DTO) containing the newly issued access and refresh tokens. */
public record RefreshResponse(
   String accessToken,
   String refreshToken,
   String tokenType,
   Integer expiresIn
) {}
