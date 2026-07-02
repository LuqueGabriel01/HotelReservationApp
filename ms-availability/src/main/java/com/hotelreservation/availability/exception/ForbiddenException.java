package com.hotelreservation.availability.exception;

/** Exception thrown when an operation is forbidden for the current user. */
public class ForbiddenException extends RuntimeException {
  public ForbiddenException(String message) {
    super(message);
  }
}
