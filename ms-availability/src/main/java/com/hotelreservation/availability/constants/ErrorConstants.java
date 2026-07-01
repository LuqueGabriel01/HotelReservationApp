package com.hotelreservation.availability.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized codes and description messages for error. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorConstants {

  /** Centralized constants for error messages. */
  public static final class Error {
    public static final String HOTEL_NOT_FOUND_PREFIX = "Hotel with id ";
    public static final String ROOM_NOT_FOUND_PREFIX = "Room with id ";
    public static final String NOT_FOUND_SUFFIX = " not found";
    public static final String NOT_FOUND_SUFFIX_IN_ = " not found in";
    public static final String MS_CATALOG_UNAVAILABLE = "Catalog is not available right now ";
    public static final String DATE_ERROR = "The dates must be different ";
    public static final String DATE_REQUIRED = "Check in and check out are required ";
    public static final String ROOM_UNAVAILABLE = "Room not available right now ";
    public static final String ROOM_BLOCK_WITH_ID = "Room block with id ";
    public static final String ROOM_BLOCKED_NOT_OWNED = "Room blocked not owned by this user ";
    public static final String BOOKING_WITH_ID = "Booking with id ";
  }
}
