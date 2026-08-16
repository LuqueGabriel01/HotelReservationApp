package com.hotelreservation.msagent.client;

import com.hotelreservation.msagent.config.properties.GeminiProperties;
import com.hotelreservation.msagent.constants.ConfigConstants;
import com.hotelreservation.msagent.dto.gemini.InteractionRequest;
import com.hotelreservation.msagent.dto.gemini.InteractionResponse;
import com.hotelreservation.msagent.exception.AiServiceUnavailableException;
import java.net.URI;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/** Client for the Gemini Interactions API. */
@Component
public class GeminiClient {

  private final WebClient geminiWebClient;
  private final GeminiProperties geminiProperties;

  public GeminiClient(
      @Qualifier("geminiWebClient") WebClient geminiWebClient, GeminiProperties geminiProperties) {
    this.geminiWebClient = geminiWebClient;
    this.geminiProperties = geminiProperties;
  }

  /**
   * Sends a request to the Gemini Interactions API and returns the resulting interaction.
   *
   * @param request the interaction request to send
   * @return a {@link Mono} emitting the interaction response
   * @throws AiServiceUnavailableException if Gemini returns an error response or cannot be reached
   */
  public Mono<InteractionResponse> createInteraction(InteractionRequest request) {
    URI uri = URI.create(geminiProperties.baseUrl() + ConfigConstants.Gemini.INTERACTIONS_PATH);

    return geminiWebClient
        .post()
        .uri(uri)
        .bodyValue(request)
        .retrieve()
        .bodyToMono(InteractionResponse.class)
        .onErrorMap(
            WebClientResponseException.class,
            ex ->
                new AiServiceUnavailableException(
                    "Gemini responded with an error: "
                        + ex.getStatusCode()
                        + " - "
                        + ex.getResponseBodyAsString(),
                    ex))
        .onErrorMap(
            WebClientRequestException.class,
            ex -> new AiServiceUnavailableException("Could not reach Gemini", ex));
  }
}
