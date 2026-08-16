package com.hotelreservation.msagent.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized package, property prefix, and integration constants for ms-agent. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigConstants {
  public static final String BASE_PACKAGE = "com.hotelreservation.msagent";

  /** {@code @ConfigurationProperties} prefixes used across the service. */
  public static final class Properties {
    public static final String CHAT = "chat";
    public static final String CATALOG = "service.catalog";
    public static final String GEMINI = "gemini";
  }

  /** Header names, values, and request paths for the Gemini Interactions API. */
  public static final class Gemini {
    public static final String X_API_HEADER = "x-goog-api-key";
    public static final String API_REVISION_HEADER = "Api-Revision";
    public static final String API_REVISION_VALUE = "2026-05-20";
    public static final String INTERACTIONS_PATH = "/interactions";
    public static final String THINKING_LEVEL_LOW = "low";
  }

  /** Request paths and query parameter names for the ms-catalog client. */
  public static final class Catalog {
    public static final String ROOMS_BY_HOTEL_PATH = "/api/hotels/{hotelId}/rooms";
    public static final String CAPACITY_PARAM = "capacity";
    public static final String MAX_PRICE_PARAM = "maxPrice";
  }
}
