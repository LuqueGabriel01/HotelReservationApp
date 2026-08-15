package com.hotelreservation.msagent.repository;

import com.hotelreservation.msagent.config.properties.ChatProperties;
import com.hotelreservation.msagent.dto.chat.ChatHistory;
import java.time.Duration;
import java.time.Instant;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/** Redis-backed store for chat session history, keyed by session id. */
@Repository
public class ChatSessionRepository {

  private final ReactiveRedisTemplate<String, ChatHistory> redisTemplate;
  private final ChatProperties chatProperties;

  public ChatSessionRepository(
      ReactiveRedisTemplate<String, ChatHistory> redisTemplate, ChatProperties chatProperties) {
    this.redisTemplate = redisTemplate;
    this.chatProperties = chatProperties;
  }

  /**
   * Retrieves the chat history for a session, creating an empty one if none exists yet.
   *
   * @param sessionId the identifier of the chat session
   * @param hotelId the identifier of the hotel the session belongs to
   * @return a {@link Mono} emitting the existing or newly created chat history
   */
  public Mono<ChatHistory> findBySessionId(String sessionId, String hotelId) {
    return redisTemplate
        .opsForValue()
        .get(sessionId)
        .defaultIfEmpty(new ChatHistory(sessionId, hotelId, null, Instant.now()));
  }

  /**
   * Persists the chat history, resetting its time-to-live to the configured session duration.
   *
   * @param chatHistory the chat history to store
   * @return a {@link Mono} emitting {@code true} if the value was set successfully
   */
  public Mono<Boolean> save(ChatHistory chatHistory) {
    Duration ttl = Duration.ofMinutes(chatProperties.sessionTtlMinutes());
    return redisTemplate.opsForValue().set(chatHistory.sessionId(), chatHistory, ttl);
  }
}
