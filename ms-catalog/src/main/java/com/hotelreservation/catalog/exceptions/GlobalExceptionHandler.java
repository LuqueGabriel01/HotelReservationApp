package com.hotelreservation.catalog.exceptions;

import com.hotelreservation.catalog.models.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Centralized exception handling interceptor.
 * */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles requests with not found id.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex){
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles requests with invalid pagination.
     */
    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFilterException(InvalidFilterException ex){
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles requests with invalid city.
     */
    @ExceptionHandler(InvalidPaginationException.class)
    public ResponseEntity<ErrorResponse> handelInvalidPaginationException(InvalidPaginationException ex){
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles any unexpected exception.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex){
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
