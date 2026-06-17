package com.hotelreservation.msgateway.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized definition of error constants and system feedback messages. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorConstants {

  /** Error messages returned to clients during failure scenarios. */
  public static final class Message {
    public static final String SERVICE_UNAVAILABLE =
        "The service you are trying to access is currently unavailable.";
    public static final String INTERNAL_ERROR = "Internal error in the Gateway ecosystem.";
    public static final String INVALID_JWT_TOKEN = "Invalid or expired JWT token";
  }
}
