package com.hotelreservation.availability.exception;

import com.hotelreservation.availability.models.dtos.response.ErrorResponse;
import java.time.Instant;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Centralized exception handling interceptor. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Handles validation errors in requests. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("Validation error");
    return buildError(HttpStatus.BAD_REQUEST, message);
  }

  /** Handles date validation in requests. */
  @ExceptionHandler(InvalidDateRangeException.class)
  public ResponseEntity<ErrorResponse> handleInvalidDateRangeException(
      InvalidDateRangeException ex) {
    return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  /** Handles room's availability. */
  @ExceptionHandler(RoomNotAvailableException.class)
  public ResponseEntity<ErrorResponse> handleRoomNotAvailableException(
      RoomNotAvailableException ex) {
    return buildError(HttpStatus.CONFLICT, ex.getMessage());
  }

  /** Handles not found exception. */
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
    return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  /** Handles external micro-services unavailability. */
  @ExceptionHandler(ExternalServiceUnavailableException.class)
  public ResponseEntity<ErrorResponse> handleExternalServiceUnavailableException(
      ExternalServiceUnavailableException ex) {
    return buildError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
  }

  /** Handles external forbidden exceptions. */
  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorResponse> handleForbiddenException(
      ExternalServiceUnavailableException ex) {
    return buildError(HttpStatus.FORBIDDEN, ex.getMessage());
  }

  /** Handles any unexpected exception. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

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
