package com.hotelreservation.msagent.service;

import com.hotelreservation.msagent.client.GeminiClient;
import com.hotelreservation.msagent.config.properties.GeminiProperties;
import com.hotelreservation.msagent.constants.ConfigConstants;
import com.hotelreservation.msagent.dto.chat.ChatHistory;
import com.hotelreservation.msagent.dto.gemini.InteractionRequest;
import com.hotelreservation.msagent.dto.gemini.InteractionResponse;
import com.hotelreservation.msagent.repository.ChatSessionRepository;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/** Orchestrates multi-turn conversations between a guest and the Gemini-backed support chatbot. */
@Service
@AllArgsConstructor
public class ChatService {

  private static final String SYSTEM_INSTRUCTION =
      """
            You are the customer support assistant for a hotel. You only answer frequently
            asked questions about the hotel's policies, check-in/check-out times, services,
            and amenities. If asked about something unrelated to the hotel or its services,
            politely explain that you can only help with questions about the stay.

            Always reply in the same language the guest used in their message.
            """;

  private final ChatSessionRepository chatSessionRepository;
  private final GeminiClient geminiClient;
  private final GeminiProperties geminiProperties;

  /**
   * Sends a guest message to Gemini and returns its reply, continuing the conversation from any
   * previous turn stored for the session.
   *
   * @param sessionId the identifier of the chat session
   * @param hotelId the identifier of the hotel the session belongs to
   * @param message the guest's message
   * @return a {@link Mono} emitting the chatbot's reply text
   */
  public Mono<String> chat(String sessionId, String hotelId, String message) {
    return chatSessionRepository
        .findBySessionId(sessionId, hotelId)
        .flatMap(history -> callGemini(history, message))
        .flatMap(result -> saveSession(sessionId, hotelId, result).thenReturn(result))
        .map(InteractionResponse::modelOutputText);
  }

  private Mono<InteractionResponse> callGemini(ChatHistory history, String message) {
    InteractionRequest request =
        new InteractionRequest(
            geminiProperties.model(),
            SYSTEM_INSTRUCTION,
            message,
            history.previousInteractionId(),
            null,
            geminiProperties.toGenerationConfig(ConfigConstants.Gemini.THINKING_LEVEL_LOW));
    return geminiClient.createInteraction(request);
  }

  private Mono<Boolean> saveSession(String sessionId, String hotelId, InteractionResponse result) {
    ChatHistory updated = new ChatHistory(sessionId, hotelId, result.id(), Instant.now());
    return chatSessionRepository.save(updated);
  }
}
