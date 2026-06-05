package com.hotelreservation.auth.exceptions;

/** Exception thrown when a request contains invalid data or syntax. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
