package com.hotelreservation.auth.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized error description messages. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorConstants {

  /** Messages providing details about the error. */
  public static final class Message {
    public static final String DEFAULT_INVALID_PASSWORD = "invalid password";
    public static final String EMAIL_ALREADY_EXIST = "Email already exists";
    public static final String EMAIL_NOT_FOUND = "Email not found";
    public static final String INVALID_CREDENTIALS = "invalid credentials";
    public static final String MISSING_INVALID_TOKEN = "Missing or invalid authorization token";
    public static final String INVALID_REFRESH_TOKEN = "Refresh token is expired or invalid";
    public static final String ACCESS_DENIED = "Access denied, insufficient permissions.";
  }
}
