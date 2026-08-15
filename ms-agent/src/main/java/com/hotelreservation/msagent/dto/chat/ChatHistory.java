package com.hotelreservation.msagent.dto.chat;

import java.time.Instant;

/** Persisted state of a chat session, stored in Redis and keyed by session id. */
public record ChatHistory(
    String sessionId, String hotelId, String previousInteractionId, Instant lastUpdated) {}
