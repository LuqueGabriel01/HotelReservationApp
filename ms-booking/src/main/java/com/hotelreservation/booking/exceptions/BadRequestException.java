package com.hotelreservation.booking.exceptions;

/** Exception thrown when a request contains invalid payload or parameters. */
public class BadRequestException extends RuntimeException {

  /**
   * Constructs a new BadRequestException with the specified detail message.
   *
   * @param message error detail message
   */
  public BadRequestException(String message) {
    super(message);
  }
}
