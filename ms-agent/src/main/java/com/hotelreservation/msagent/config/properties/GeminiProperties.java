package com.hotelreservation.msagent.config.properties;

import static com.hotelreservation.msagent.constants.ConfigConstants.Properties.GEMINI;

import com.hotelreservation.msagent.dto.gemini.GenerationConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Connection, model, and generation settings for the Gemini Interactions API. */
@ConfigurationProperties(prefix = GEMINI)
public record GeminiProperties(
    String apiKey,
    String baseUrl,
    String model,
    Double temperature,
    Integer maxOutputTokens,
    Long timeoutMs) {

  /**
   * Builds the {@link GenerationConfig} to send with a Gemini interaction, based on the configured
   * temperature and output token limit.
   *
   * @param thinkingLevel the thinking budget to request for this interaction, or {@code null} to
   *     let Gemini use its default
   * @return the generation configuration derived from these properties
   */
  public GenerationConfig toGenerationConfig(String thinkingLevel) {
    return new GenerationConfig(temperature, maxOutputTokens, thinkingLevel);
  }
}
