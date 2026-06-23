package com.hotelreservation.catalog.exceptions;

/** Exception thrown when the filter is invalid. */
public class InvalidFilterException extends RuntimeException {
  public InvalidFilterException(String message) {
    super(message);
  }
}
