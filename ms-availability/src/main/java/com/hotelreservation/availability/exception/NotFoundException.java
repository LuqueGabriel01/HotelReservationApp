package com.hotelreservation.availability.exception;

/** Exception thrown when the id is not found. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
