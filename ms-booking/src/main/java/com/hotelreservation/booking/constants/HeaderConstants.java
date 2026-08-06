package com.hotelreservation.booking.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Utility class holding HTTP header constants. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderConstants {
  public static final String X_USER_ID = "X-User-Id";
  public static final String X_USER_ROLE = "X-User-Role";
  public static final String ROLE_USER = "ROLE_USER";
  public static final String ROLE_ADMIN = "ROLE_ADMIN";
}
