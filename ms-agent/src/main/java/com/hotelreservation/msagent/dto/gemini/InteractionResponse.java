package com.hotelreservation.msagent.dto.gemini;

import java.util.List;

/**
 * Response body returned by the Gemini Interactions API {@code POST /v1beta/interactions} endpoint.
 */
public record InteractionResponse(String id, String status, List<Step> steps) {

  /**
   * Concatenates the text content of every {@code model_output} step in this response, skipping
   * intermediate steps such as {@code thought}.
   *
   * @return the concatenated model output text, or an empty string if none is present
   */
  public String modelOutputText() {
    if (steps == null) {
      return "";
    }

    StringBuilder text = new StringBuilder();
    for (Step step : steps) {
      if (!"model_output".equals(step.type()) || step.content() == null) {
        continue;
      }
      for (ContentPart part : step.content()) {
        if (part.text() != null) {
          text.append(part.text());
        }
      }
    }
    return text.toString();
  }
}
