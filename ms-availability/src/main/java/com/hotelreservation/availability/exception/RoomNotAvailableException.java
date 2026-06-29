package com.hotelreservation.availability.exception;

/** Exception thrown when the room is not available. */
public class RoomNotAvailableException extends RuntimeException {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}
