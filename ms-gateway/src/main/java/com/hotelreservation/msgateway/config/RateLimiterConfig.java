package com.hotelreservation.msgateway.config;

import com.hotelreservation.msgateway.config.properties.RateLimiterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Configuration class to set up the rate limiting mechanism for the gateway. Configures the key
 * resolver based on client IP and the Redis-backed rate limiter.
 */
@Configuration
@RequiredArgsConstructor
public class RateLimiterConfig {

  private final RateLimiterProperties rateLimiterProperties;

  /**
   * Configures a {@link KeyResolver} that identifies clients by their remote IP address.
   *
   * @return a {@link KeyResolver} instance extracting the host address from the request.
   */
  @Bean
  public KeyResolver ipAddressKeyResolver() {
    return exchange ->
        Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
  }

  /**
   * Creates a {@link RedisRateLimiter} instance using the configured properties.
   *
   * @return a {@link RedisRateLimiter} configured with replenish rate, burst capacity, and request
   *     token costs.
   */
  @Bean
  public RedisRateLimiter redisRateLimiterConfig() {
    return new RedisRateLimiter(
        rateLimiterProperties.replenishRate(),
        rateLimiterProperties.burstCapacity(),
        rateLimiterProperties.requestToken());
  }
}
