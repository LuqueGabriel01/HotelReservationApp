package com.hotelreservation.msgateway.exceptions;

/** Exception thrown when JWT authentication or token validation fails within the gateway. */
public class JwtAuthenticationException extends RuntimeException {

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message explaining the reason for the failure.
   */
  public JwtAuthenticationException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message explaining the reason for the failure.
   * @param cause the underlying cause of the exception.
   */
  public JwtAuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
