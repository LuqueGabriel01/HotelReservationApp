package com.hotelreservation.availability.exception;

/** Exception thrown when it fails to connect to the other services. */
public class ExternalServiceUnavailableException extends RuntimeException {
    public ExternalServiceUnavailableException(String message) {
        super(message);
    }
}
