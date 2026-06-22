package com.hotelreservation.catalog.exceptions;

/** Exception thrown when the pagination is invalid. */
public class InvalidPaginationException extends RuntimeException {
  public InvalidPaginationException(String message) {
    super(message);
  }
}
