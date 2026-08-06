package com.hotelreservation.booking.exceptions;

import com.hotelreservation.booking.models.dtos.response.ErrorResponse;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler to capture domain exceptions and transform them into standard error
 * responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles {@link BadRequestException} and transforms it into a 400 Bad Request HTTP response.
   *
   * @param ex caught exception
   * @return response entity containing standard error details
   */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
    return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  /**
   * Handles {@link NotFoundException} and transforms it into a 404 Not Found HTTP response.
   *
   * @param ex caught exception
   * @return response entity containing standard error details
   */
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
    return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  /**
   * Handles {@link ConflictException} and transforms it into a 409 Conflict HTTP response.
   *
   * @param ex caught exception
   * @return response entity containing standard error details
   */
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflictError(ConflictException ex) {
    return buildError(HttpStatus.CONFLICT, ex.getMessage());
  }

  /**
   * Handles {@link ForbiddenException} and transforms it into a 403 Forbidden HTTP response.
   *
   * @param ex caught exception
   * @return response entity containing standard error details
   */
  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
    return buildError(HttpStatus.FORBIDDEN, ex.getMessage());
  }

  /**
   * Builds a standardized {@link ErrorResponse} wrapper for a given HTTP status and error message.
   *
   * @param status HTTP status code and name
   * @param description error message payload
   * @return response entity wrapping the formatted error
   */
  private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String description) {

    Instant now = Instant.now();

    ErrorResponse error =
        ErrorResponse.builder()
            .code(status.value())
            .name(status.name())
            .description(description)
            .timestamp(now)
            .build();

    return ResponseEntity.status(status).body(error);
  }
}
