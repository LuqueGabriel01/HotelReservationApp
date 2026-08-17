package com.hotelreservation.msagent.config;

import com.hotelreservation.msagent.dto.chat.ChatHistory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/** Redis configuration for storing chat session history. */
@Configuration
public class RedisConfig {

  /**
   * Builds the reactive Redis template used to read and write {@link ChatHistory} records, keyed by
   * session id.
   *
   * @param connectionFactory the reactive Redis connection factory
   * @return the configured reactive Redis template
   */
  @Bean
  public ReactiveRedisTemplate<String, ChatHistory> chatHistoryRedisTemplate(
      ReactiveRedisConnectionFactory connectionFactory) {

    StringRedisSerializer keySerializer = new StringRedisSerializer();
    JacksonJsonRedisSerializer<ChatHistory> valueSerializer =
        new JacksonJsonRedisSerializer<>(ChatHistory.class);

    RedisSerializationContext<String, ChatHistory> context =
        RedisSerializationContext.<String, ChatHistory>newSerializationContext(keySerializer)
            .value(valueSerializer)
            .build();

    return new ReactiveRedisTemplate<>(connectionFactory, context);
  }
}
