package com.hotelreservation.msagent.exception;

/** Thrown when ms-catalog returns an error or cannot be reached. */
public class CatalogUnavailableException extends RuntimeException {

  public CatalogUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
