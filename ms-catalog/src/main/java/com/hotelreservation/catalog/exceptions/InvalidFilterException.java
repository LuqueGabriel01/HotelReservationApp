package com.hotelreservation.catalog.exceptions;

/**
 * Exception thrown when the id is not found.
 */
public class InvalidFilterException extends RuntimeException {
    public InvalidFilterException(String message) {
        super(message);
    }
}
