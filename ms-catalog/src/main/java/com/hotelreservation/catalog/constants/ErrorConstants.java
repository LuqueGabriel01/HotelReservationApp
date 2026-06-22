package com.hotelreservation.catalog.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized codes and description messages for error. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorConstants {

  /** Centralized constants for error messages. */
  public static final class Error {
    public static final String HOTEL_NOT_FOUND_PREFIX = "Hotel with id ";
    public static final String ROOM_NOT_FOUND_PREFIX = "Room with id ";
    public static final String IMAGE_NOT_FOUND_PREFIX = "Image with id ";
    public static final String NOT_FOUND_SUFFIX = " not found";
    public static final String NOT_FOUND_SUFFIX_IN_ = " not found in";
  }
}
