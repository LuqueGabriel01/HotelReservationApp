package com.hotelreservation.catalog.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized configuration constants and property prefixes for the application. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigConstants {

  public static final String BASE_PACKAGE = "com.hotelreservation.catalog";

  /** Configuration property prefixes used for binding external properties. */
  public static final class Properties {
    public static final String HOTEL_CATALOG_CACHE = "hotel.catalog";
    public static final String CLOUDINARY = "cloudinary";
  }
}
