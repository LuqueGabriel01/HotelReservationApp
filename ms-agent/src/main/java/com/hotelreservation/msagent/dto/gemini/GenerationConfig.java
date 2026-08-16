package com.hotelreservation.msagent.dto.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model configuration parameters for a Gemini interaction, sent under {@code generation_config}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerationConfig(
    Double temperature,
    @JsonProperty("max_output_tokens") Integer maxOutputTokens,
    @JsonProperty("thinking_level") String thinkingLevel) {}
