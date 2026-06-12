package com.hotelreservation.auth.exceptions;

import com.hotelreservation.auth.constants.ErrorConstants;
import com.hotelreservation.auth.models.dtos.response.ErrorResponse;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Centralized exception handling interceptor. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Handles validation errors triggered by @Valid annotations. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    fieldError.getDefaultMessage() != null
                        ? fieldError.getDefaultMessage()
                        : fieldError.getField())
            .findFirst()
            .orElse(ErrorConstants.Message.INVALID_CREDENTIALS);
    return buildError(HttpStatus.BAD_REQUEST, message);
  }

  /** Handles client requests with invalid data or syntax. */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handlerBadRequestException(BadRequestException ex) {
    return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  /** Handles requests that conflict with the current state of the server. */
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handlerConflictException(ConflictException ex) {
    return buildError(HttpStatus.CONFLICT, ex.getMessage());
  }

  /** Handles missing, expired, or invalid authentication errors. */
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handlerUnauthorizedException(UnauthorizedException ex) {
    return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
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
