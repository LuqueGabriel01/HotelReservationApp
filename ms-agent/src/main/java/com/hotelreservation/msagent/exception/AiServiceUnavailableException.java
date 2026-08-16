package com.hotelreservation.msagent.exception;

/** Thrown when the Gemini AI service returns an error or cannot be reached. */
public class AiServiceUnavailableException extends RuntimeException {

  public AiServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
