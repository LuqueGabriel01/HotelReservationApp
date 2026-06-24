package com.hotelreservation.availability.exception;

import com.hotelreservation.availability.models.dtos.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Objects;

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
  public ResponseEntity<ErrorResponse> handleInvalidDateRangeException(InvalidDateRangeException ex){
    return buildError(HttpStatus.BAD_REQUEST, "Invalid date range request");
  }

  /** Handles room's availability. */
  @ExceptionHandler(RoomNotAvailableException.class)
  public ResponseEntity<ErrorResponse> handleRoomNotAvailableException(RoomNotAvailableException ex){
    return buildError(HttpStatus.NOT_FOUND, "The room is not available");
  }

  /** Handles not found room block. */
  @ExceptionHandler(RoomBlockNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleRoomBlockNotFoundException(RoomBlockNotFoundException ex){
    return buildError(HttpStatus.NOT_FOUND, "The room block is not found");
  }

  /** Handles any unexpected exception. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
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
