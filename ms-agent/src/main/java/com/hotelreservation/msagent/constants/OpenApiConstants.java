package com.hotelreservation.msagent.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized codes and description messages for OpenAPI. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenApiConstants {

  /** Centralized codes for HttpStatus. */
  public static final class Code {
    public static final String SUCCESS = "200";
    public static final String BAD_REQUEST = "400";
    public static final String NOT_FOUND = "404";
    public static final String SERVICE_UNAVAILABLE = "503";
  }
}
