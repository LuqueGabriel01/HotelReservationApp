package com.hotelreservation.booking.exceptions;

/**
 * Exception thrown when an authenticated user attempts an operation without sufficient permissions.
 */
public class ForbiddenException extends RuntimeException {

  /**
   * Constructs a new ForbiddenException with the specified detail message.
   *
   * @param message error detail message
   */
  public ForbiddenException(String message) {
    super(message);
  }
}
