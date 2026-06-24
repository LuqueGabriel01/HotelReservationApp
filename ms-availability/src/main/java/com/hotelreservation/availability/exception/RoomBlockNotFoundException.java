package com.hotelreservation.availability.exception;

public class RoomBlockNotFoundException extends RuntimeException {
    public RoomBlockNotFoundException(String message) {
        super(message);
    }
}
