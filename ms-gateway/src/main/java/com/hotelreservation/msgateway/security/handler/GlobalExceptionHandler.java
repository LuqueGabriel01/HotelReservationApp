package com.hotelreservation.msgateway.security.handler;

import com.hotelreservation.msgateway.constants.ErrorConstants;
import com.hotelreservation.msgateway.exceptions.JwtAuthenticationException;
import com.hotelreservation.msgateway.model.ErrorResponse;
import java.net.ConnectException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

/**
 * Global exception handler that intercepts unhandled errors across the gateway ecosystem. Formats
 * exception details into a standardized {@link ErrorResponse} payload.
 */
@Component
@NullMarked
@RequiredArgsConstructor
public class GlobalExceptionHandler implements WebExceptionHandler, Ordered {

  private final ObjectMapper objectMapper;

  /**
   * Intercepts and processes the thrown exception, writing a structured JSON response.
   *
   * @param exchange the current server exchange.
   * @param ex the exception thrown during request processing.
   * @return a {@link Mono} signaling completion of the error handling response.
   */
  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

    ServerHttpResponse response = exchange.getResponse();

    if (response.isCommitted()) {
      return Mono.error(ex);
    }

    HttpStatus status = determineStatus(ex);
    String message = determineMessage(ex);

    response.setStatusCode(status);
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    ErrorResponse error = ErrorResponse.of(status.value(), status.getReasonPhrase(), message);

    return response.writeWith(
        Mono.fromSupplier(
            () -> {
              DataBufferFactory bufferFactory = response.bufferFactory();
              try {
                byte[] body = objectMapper.writeValueAsBytes(error);
                return bufferFactory.wrap(body);
              } catch (JwtAuthenticationException e) {
                return bufferFactory.wrap(new byte[0]);
              }
            }));
  }

  private HttpStatus determineStatus(Throwable e) {

    if (e instanceof ResponseStatusException rse) {
      return HttpStatus.valueOf(rse.getStatusCode().value());
    }
    if (e instanceof ConnectException) {
      return HttpStatus.SERVICE_UNAVAILABLE;
    }

    if (e instanceof JwtAuthenticationException) {
      return HttpStatus.UNAUTHORIZED;
    }

    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  private String determineMessage(Throwable ex) {
    if (ex instanceof ResponseStatusException rse) {
      String reason = rse.getReason();
      return reason != null ? reason : ex.getMessage();
    }
    if (ex instanceof ConnectException) {
      return ErrorConstants.Message.SERVICE_UNAVAILABLE;
    }

    return ex.getMessage() != null ? ex.getMessage() : ErrorConstants.Message.INTERNAL_ERROR;
  }

  /**
   * Defines the execution order of the exception handler in the WebFlux chain.
   *
   * @return the precedence integer value.
   */
  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 1000;
  }
}
