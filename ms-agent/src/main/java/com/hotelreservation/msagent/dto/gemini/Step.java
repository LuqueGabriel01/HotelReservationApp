package com.hotelreservation.msagent.dto.gemini;

import java.util.List;

/** A single step (e.g. a thought or a model output) within a Gemini {@link InteractionResponse}. */
public record Step(String type, String status, List<ContentPart> content) {}
