package com.hotelreservation.availability.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized constants for all the services. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientConstants {

  public static final String BASE_PACKAGE_CLIENT = "com.hotelreservation.availability.client";

  /** Catalog microservice. */
  public static final class Catalog {
    public static final String MS_CATALOG = "ms-catalog";
  }
}
