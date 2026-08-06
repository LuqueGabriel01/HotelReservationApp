package com.hotelreservation.booking.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Utility class holding error messages and error code constants. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorConstants {

  /** Constants for application error messages. */
  public static final class Message {
    public static final String BOOKING_ALREADY_EXISTS = "Booking already exists";
    public static final String BOOKING_EXPIRED = "Lock expired, restart the process";
    public static final String BOOKING_NOT_FOUND = "Not found in the database";
    public static final String BOOKING_NOT_USER = "The booking is not made by the user";
    public static final String BOOKING_CAN_NOT_CANCELLED = "The booking cannot be cancelled";
    public static final String BOOKING_FORBIDDEN = "booking Forbidden";
  }
}
