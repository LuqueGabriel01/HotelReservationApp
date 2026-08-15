package com.hotelreservation.msagent.dto.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/** Enforces that a Gemini interaction response complies with a given JSON schema. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseFormat(
    String type, @JsonProperty("mime_type") String mimeType, Map<String, Object> schema) {}
