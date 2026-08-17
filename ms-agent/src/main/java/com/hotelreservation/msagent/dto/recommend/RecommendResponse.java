package com.hotelreservation.msagent.dto.recommend;

import java.util.List;

/** Data transfer object (DTO) wrapping the ranked list of room recommendations. */
public record RecommendResponse(List<RoomRecommendation> recommendations) {}
