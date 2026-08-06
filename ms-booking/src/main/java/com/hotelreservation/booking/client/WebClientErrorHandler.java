package com.hotelreservation.booking.client;

import com.hotelreservation.booking.exceptions.NotFoundException;
import javax.naming.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

/** Utility handler for WebClient HTTP error responses. */
@Slf4j
public final class WebClientErrorHandler {
  private WebClientErrorHandler() {}

  /**
   * Handles 4xx client errors by mapping the response body to a NotFoundException.
   *
   * @param response reactive client HTTP response
   * @return Mono signaling a NotFoundException
   */
  public static Mono<? extends Throwable> handleNotFound(ClientResponse response) {
    return response
        .bodyToMono(String.class)
        .defaultIfEmpty("")
        .flatMap(body -> Mono.error(new NotFoundException("Booking not found: " + body)));
  }

  /**
   * Handles 5xx server errors by logging the issue and throwing a ServiceUnavailableException.
   *
   * @param response reactive client HTTP response
   * @return Mono signaling a ServiceUnavailableException
   */
  public static Mono<? extends Throwable> handleServerError(ClientResponse response) {
    return response
        .bodyToMono(String.class)
        .defaultIfEmpty("")
        .flatMap(
            body -> {
              log.error("External service 5xx. Status: {}, Body: {}", response.statusCode(), body);
              return Mono.error(new ServiceUnavailableException());
            });
  }
}
