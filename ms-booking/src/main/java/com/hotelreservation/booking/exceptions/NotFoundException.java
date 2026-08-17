package com.hotelreservation.booking.exceptions;

/** Exception thrown when a requested resource cannot be found in the system. */
public class NotFoundException extends RuntimeException {

  /**
   * Constructs a new NotFoundException with the specified detail message.
   *
   * @param message error detail message
   */
  public NotFoundException(String message) {
    super(message);
  }
}
