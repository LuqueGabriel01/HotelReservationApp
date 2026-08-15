package com.hotelreservation.msagent.dto.chat;

import java.time.Instant;

/** Data transfer object (DTO) representing the chatbot's reply to a guest message. */
public record ChatResponse(String sessionId, String reply, Instant timestamp) {}
