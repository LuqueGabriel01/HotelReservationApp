package com.hotelreservation.booking.constants;

/** Centralized codes and description messages for OpenAPI. */
public final class OpenApiConstants {
  public static final String BEARER_AUTH = "BearerAuth";

  /** Centralized messages for OpenAPI. */
  public static final class Message {
    public static final String FORBIDDEN_MESSAGE = "Forbidden";
  }

  /** Centralized codes for OpenAPI. */
  public static final class Code {
    public static final String SUCCESS = "200";
    public static final String CREATED = "201";
    public static final String BAD_REQUEST = "400";
    public static final String FORBIDDEN = "403";
    public static final String NOT_FOUND = "404";
    public static final String CONFLICT = "409";
  }
}
