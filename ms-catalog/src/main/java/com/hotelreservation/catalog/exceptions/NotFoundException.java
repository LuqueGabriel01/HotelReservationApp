package com.hotelreservation.catalog.exceptions;

/** Exception thrown when the id is not found. */
public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
