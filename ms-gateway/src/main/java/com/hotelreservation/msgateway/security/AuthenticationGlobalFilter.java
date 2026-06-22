package com.hotelreservation.msgateway.security;

import com.hotelreservation.msgateway.constants.ErrorConstants;
import com.hotelreservation.msgateway.constants.HeaderConstants;
import com.hotelreservation.msgateway.exceptions.JwtAuthenticationException;
import com.hotelreservation.msgateway.services.JwtService;
import com.hotelreservation.msgateway.validator.RouteValidator;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter responsible for verifying JWT access tokens on protected routes. Mutates incoming
 * requests to propagate authenticated user identity and roles downstream.
 */
@Component
@NullMarked
@RequiredArgsConstructor
public class AuthenticationGlobalFilter implements GlobalFilter, Ordered {

  private final RouteValidator routeValidator;
  private final JwtService jwtService;

  /**
   * Intercepts the request to validate authentication credentials if the route is protected.
   *
   * @param exchange the current server web exchange context.
   * @param chain the gateway filter chain to delegate execution to.
   * @return a {@link Mono} indicating request processing completion.
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    if (routeValidator.isPublic(exchange.getRequest())) {
      return chain.filter(exchange);
    }

    String authHeader =
        exchange.getRequest().getHeaders().getFirst(HeaderConstants.Security.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith(HeaderConstants.Security.BEARER_PREFIX)) {
      return Mono.error(new JwtAuthenticationException(ErrorConstants.Message.INVALID_JWT_TOKEN));
    }

    String token = authHeader.substring(HeaderConstants.Security.BEARER_PREFIX.length()).trim();

    try {
      jwtService.validateToken(token);
    } catch (JwtAuthenticationException e) {
      return Mono.error(e);
    }

    String headerUserId = jwtService.extractUserId(token);
    String headerRole = jwtService.extractRole(token);

    ServerWebExchange mutatedExchange =
        exchange
            .mutate()
            .request(
                request ->
                    request
                        .header(HeaderConstants.Security.X_USER_ID, headerUserId)
                        .header(HeaderConstants.Security.X_USER_ROLE, headerRole))
            .build();

    System.out.println(
        mutatedExchange.getRequest().getHeaders().get(HeaderConstants.Security.X_USER_ID));
    System.out.println(
        mutatedExchange.getRequest().getHeaders().get(HeaderConstants.Security.X_USER_ROLE));

    return chain.filter(mutatedExchange);
  }

  /**
   * Defines the filter execution priority in the gateway infrastructure chain.
   *
   * @return the precedence integer value.
   */
  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
