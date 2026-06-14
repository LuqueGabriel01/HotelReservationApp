package com.hotelreservation.auth.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Utility class for security-related constants. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {

  /** Constants representing security field names and tokens. */
  public static final class Field {
    public static final String USERNAME = "username";
    public static final String ROLE = "role";
    public static final String BEARER = "Bearer ";
  }
}
