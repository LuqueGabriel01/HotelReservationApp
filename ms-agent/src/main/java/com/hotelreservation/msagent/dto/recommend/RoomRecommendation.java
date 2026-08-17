package com.hotelreservation.msagent.dto.recommend;

import java.math.BigDecimal;

/** Data transfer object (DTO) representing a recommended room, merging catalog and Gemini data. */
public record RoomRecommendation(
    String roomId,
    String roomType,
    BigDecimal pricePerNight,
    Integer capacity,
    Integer score,
    String reason) {}
