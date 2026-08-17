package com.hotelreservation.booking.exceptions;

/** Exception thrown when a request conflicts with the current state of a resource. */
public class ConflictException extends RuntimeException {

  /**
   * Constructs a new ConflictException with the specified detail message.
   *
   * @param message error detail message
   */
  public ConflictException(String message) {
    super(message);
  }
}
