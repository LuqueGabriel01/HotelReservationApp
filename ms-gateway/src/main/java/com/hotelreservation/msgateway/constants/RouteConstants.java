package com.hotelreservation.msgateway.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized constants for API Gateway routing identifiers and resilience structures. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RouteConstants {

  /** Unique identifiers for each microservice route within the gateway. */
  public static final class Id {
    public static final String AUTH = "ms-auth-route";
    public static final String AGENT = "ms-agent-route";
    public static final String AVAILABILITY = "ms-availability-route";
    public static final String BOOKING = "ms-booking-route";
    public static final String CATALOG = "ms-catalog-route";
  }

  /** Resilience settings and configurations for gateway fault tolerance. */
  public static final class CircuitBreaker {

    /** Dedicated identifiers for individual microservice circuit breakers. */
    public static final class Name {
      public static final String AUTH = "auth-cb";
      public static final String AGENT = "agent-cb";
      public static final String AVAILABILITY = "availability-cb";
      public static final String BOOKING = "booking-cb";
      public static final String CATALOG = "catalog-cb";
    }
  }
}
