package com.hotelreservation.msagent.dto.recommend;

/** A single room ranking returned by Gemini's structured recommendation output. */
public record GeminiRecommendationItem(String roomId, Integer score, String reason) {}
