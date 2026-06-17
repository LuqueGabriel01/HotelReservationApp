package com.hotelreservation.msgateway.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized configuration constants and property prefixes for the application. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigConstants {

  public static final String BASE_PACKAGE = "com.hotelreservation.msgateway";

  /** Configuration property prefixes used for binding external properties. */
  public static final class Properties {
    public static final String HOTEL_GATEWAY_CORS = "hotel.gateway.cors";
    public static final String HOTEL_GATEWAY_SERVICE = "hotel.gateway.service";
    public static final String HOTEL_GATEWAY_JWT = "hotel.gateway.jwt";
    public static final String HOTEL_GATEWAY_RATE_LIMITER = "hotel.gateway.rate-limiter";
  }
}
