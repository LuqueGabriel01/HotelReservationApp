package com.hotelreservation.msagent.exception;

/** Thrown when no rooms match the recommendation request's criteria for a hotel. */
public class NoAvailableRoomsException extends RuntimeException {

  public NoAvailableRoomsException(String message) {
    super(message);
  }
}
