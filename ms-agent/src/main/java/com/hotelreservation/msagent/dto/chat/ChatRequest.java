package com.hotelreservation.msagent.dto.chat;

import jakarta.validation.constraints.NotBlank;

/** Data transfer object (DTO) representing an incoming chat message from a guest. */
public record ChatRequest(String sessionId, @NotBlank String hotelId, @NotBlank String message) {}
