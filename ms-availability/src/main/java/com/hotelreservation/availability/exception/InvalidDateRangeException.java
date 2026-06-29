package com.hotelreservation.availability.exception;

/** Exception thrown when the dates are invalid. */
public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}
