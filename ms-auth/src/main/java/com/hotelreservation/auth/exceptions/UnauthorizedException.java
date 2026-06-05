package com.hotelreservation.auth.exceptions;

/** Exception thrown when authentication fails or is missing. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
