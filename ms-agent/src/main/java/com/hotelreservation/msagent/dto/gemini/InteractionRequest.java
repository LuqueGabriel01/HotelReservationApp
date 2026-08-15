package com.hotelreservation.msagent.dto.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Request body for the Gemini Interactions API {@code POST /v1beta/interactions} endpoint. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InteractionRequest(
    String model,
    @JsonProperty("system_instruction") String systemInstruction,
    String input,
    @JsonProperty("previous_interaction_id") String previousInteractionId,
    @JsonProperty("response_format") ResponseFormat responseFormat,
    @JsonProperty("generation_config") GenerationConfig generationConfig) {}
