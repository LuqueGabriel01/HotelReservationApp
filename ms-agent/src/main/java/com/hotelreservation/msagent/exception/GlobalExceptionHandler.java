package com.hotelreservation.msagent.exception;

import com.hotelreservation.msagent.dto.ErrorResponse;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

/** Maps domain and validation exceptions raised by ms-agent to HTTP error responses. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles failures to reach or get a valid response from the Gemini AI service.
   *
   * @param ex the exception raised while calling Gemini
   * @return a {@code 503 Service Unavailable} response
   */
  @ExceptionHandler(AiServiceUnavailableException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleAiServiceUnavailable(
      AiServiceUnavailableException ex) {
    return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
  }

  /**
   * Handles failures to reach or get a valid response from ms-catalog.
   *
   * @param ex the exception raised while calling ms-catalog
   * @return a {@code 503 Service Unavailable} response
   */
  @ExceptionHandler(CatalogUnavailableException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleCatalogUnavailable(
      CatalogUnavailableException ex) {
    return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
  }

  /**
   * Handles request body validation failures.
   *
   * @param ex the binding exception raised by an invalid request payload
   * @return a {@code 400 Bad Request} response with the first validation error found, or a generic
   *     message if none is available
   */
  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleValidationErrors(WebExchangeBindException ex) {
    String description =
        ex.getFieldErrors().isEmpty()
            ? "Invalid request"
            : ex.getFieldErrors().getFirst().getDefaultMessage();
    return buildResponse(HttpStatus.BAD_REQUEST, description);
  }

  /**
   * Handles the case where no rooms match a recommendation request.
   *
   * @param ex the exception raised when the catalog query returns no rooms
   * @return a {@code 404 Not Found} response
   */
  @ExceptionHandler(NoAvailableRoomsException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleNoAvailableRooms(NoAvailableRoomsException ex) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  /**
   * Handles any exception not covered by the other handlers.
   *
   * @param ex the unexpected exception
   * @return a {@code 500 Internal Server Error} response with a generic message
   */
  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ErrorResponse>> handleUnexpectedError(Exception ex) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
  }

  private Mono<ResponseEntity<ErrorResponse>> buildResponse(HttpStatus status, String message) {
    ErrorResponse errorResponse =
        new ErrorResponse(status.value(), status.getReasonPhrase(), message, Instant.now());
    return Mono.just(ResponseEntity.status(status).body(errorResponse));
  }
}
