package com.hotelreservation.msgateway.config;

import com.hotelreservation.msgateway.config.properties.ServicesProperties;
import com.hotelreservation.msgateway.constants.ApiPaths;
import com.hotelreservation.msgateway.constants.RouteConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that defines the API gateway routing rules. Maps incoming HTTP requests to
 * their respective microservices while applying resilience and traffic control patterns.
 */
@Configuration
@RequiredArgsConstructor
public class RouteConfig {

  private final ServicesProperties servicesProperties;
  private final RedisRateLimiter redisRateLimiterConfig;
  private final KeyResolver ipAddressKeyResolver;

  /**
   * Configures the {@link RouteLocator} to orchestrate routes for all microservices. Each route is
   * equipped with a Circuit Breaker for fault tolerance and a Request Rate Limiter for traffic
   * shaping.
   *
   * @param builder The {@link RouteLocatorBuilder} used to construct the gateway routes.
   * @return a {@link RouteLocator} instance containing the configured gateway routes.
   */
  @Bean
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder
        .routes()
        .route(
            RouteConstants.Id.AUTH,
            r ->
                r.path(ApiPaths.GatewaysRoutes.AUTH)
                    .filters(
                        f ->
                            f.circuitBreaker(
                                    c ->
                                        c.setName(RouteConstants.CircuitBreaker.Name.AUTH)
                                            .setFallbackUri(
                                                ApiPaths.GatewaysRoutes.FORWARD_FALLBACK))
                                .requestRateLimiter(
                                    rate ->
                                        rate.setRateLimiter(redisRateLimiterConfig)
                                            .setKeyResolver(ipAddressKeyResolver)))
                    .uri(servicesProperties.auth()))
        .route(
            RouteConstants.Id.AGENT,
            r ->
                r.path(ApiPaths.GatewaysRoutes.AI)
                    .filters(
                        f ->
                            f.circuitBreaker(
                                    c ->
                                        c.setName(RouteConstants.CircuitBreaker.Name.AGENT)
                                            .setFallbackUri(
                                                ApiPaths.GatewaysRoutes.FORWARD_FALLBACK))
                                .requestRateLimiter(
                                    rate ->
                                        rate.setRateLimiter(redisRateLimiterConfig)
                                            .setKeyResolver(ipAddressKeyResolver)))
                    .uri(servicesProperties.agent()))
        .route(
            RouteConstants.Id.AVAILABILITY,
            r ->
                r.path(ApiPaths.GatewaysRoutes.AVAILABILITY)
                    .filters(
                        f ->
                            f.circuitBreaker(
                                    c ->
                                        c.setName(RouteConstants.CircuitBreaker.Name.AVAILABILITY)
                                            .setFallbackUri(
                                                ApiPaths.GatewaysRoutes.FORWARD_FALLBACK))
                                .requestRateLimiter(
                                    rate ->
                                        rate.setRateLimiter(redisRateLimiterConfig)
                                            .setKeyResolver(ipAddressKeyResolver)))
                    .uri(servicesProperties.availability()))
        .route(
            RouteConstants.Id.BOOKING,
            r ->
                r.path(ApiPaths.GatewaysRoutes.BOOKING)
                    .filters(
                        f ->
                            f.circuitBreaker(
                                    c ->
                                        c.setName(RouteConstants.CircuitBreaker.Name.BOOKING)
                                            .setFallbackUri(
                                                ApiPaths.GatewaysRoutes.FORWARD_FALLBACK))
                                .requestRateLimiter(
                                    rate ->
                                        rate.setRateLimiter(redisRateLimiterConfig)
                                            .setKeyResolver(ipAddressKeyResolver)))
                    .uri(servicesProperties.booking()))
        .route(
            RouteConstants.Id.CATALOG,
            r ->
                r.path(ApiPaths.GatewaysRoutes.CATALOG)
                    .filters(
                        f ->
                            f.circuitBreaker(
                                    c ->
                                        c.setName(RouteConstants.CircuitBreaker.Name.CATALOG)
                                            .setFallbackUri(
                                                ApiPaths.GatewaysRoutes.FORWARD_FALLBACK))
                                .requestRateLimiter(
                                    rate ->
                                        rate.setRateLimiter(redisRateLimiterConfig)
                                            .setKeyResolver(ipAddressKeyResolver)))
                    .uri(servicesProperties.catalog()))
        .build();
  }
}
