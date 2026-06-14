package com.hotelreservation.auth.exceptions;

/** Exception thrown when a request conflicts with the current state of the server. */
public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
