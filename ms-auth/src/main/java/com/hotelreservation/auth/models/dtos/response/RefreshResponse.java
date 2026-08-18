package com.hotelreservation.auth.models.dtos.response;

import lombok.Builder;

/** Data transfer object (DTO) containing the newly issued access token and session details. */
@Builder
public record RefreshResponse(String accessToken, String tokenType, Integer expiresIn) {}
