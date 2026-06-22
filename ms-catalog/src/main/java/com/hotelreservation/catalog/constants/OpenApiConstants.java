package com.hotelreservation.catalog.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized codes and description messages for OpenAPI. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenApiConstants {

  /** Centralized codes for HttpStatus. */
  public static final class Code {
    public static final String SUCCESS = "200";
    public static final String CREATED = "202";
    public static final String NO_CONTENT = "204";
    public static final String BAD_REQUEST = "400";
    public static final String NOT_FOUND = "404";
    public static final String CONFLICT = "409";
    public static final String INTERNAL_SERVER_ERROR = "500";
  }

  /** Centralized constants for examples. */
  public static final class Example {
    public static final String EXAMPLE_UUID = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
    public static final String HOTEL_UUID = "Hotel UUID";
    public static final String ROOM_UUID = "Room UUID";
    public static final String IMAGE_UUID = "Image UUID";
  }
}
