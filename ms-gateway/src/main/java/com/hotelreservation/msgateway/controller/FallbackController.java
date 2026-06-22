package com.hotelreservation.msgateway.controller;

import com.hotelreservation.msgateway.constants.ApiPaths;
import com.hotelreservation.msgateway.constants.ErrorConstants;
import com.hotelreservation.msgateway.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * A reactive controller responsible for handling fallback requests when a downstream microservice
 * fails to respond or the Circuit Breaker is open.
 */
@RestController
public class FallbackController {

  /**
   * Intercepts internal redirects from the Circuit Breaker and returns a standardized error
   * response with a 503 Service Unavailable status.
   *
   * @return a {@link Mono} containing the {@link ResponseEntity} with the error details
   */
  @RequestMapping(ApiPaths.GatewaysRoutes.FALLBACK)
  public Mono<ResponseEntity<ErrorResponse>> handleFallback() {

    ErrorResponse error =
        ErrorResponse.of(
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            HttpStatus.SERVICE_UNAVAILABLE.name(),
            ErrorConstants.Message.SERVICE_UNAVAILABLE);

    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error));
  }
}
