package com.hotelreservation.availability.constants;

import lombok.NoArgsConstructor;

/** Centralized definition of API endpoint paths. */
@NoArgsConstructor
public final class ApiPaths {

  public static final String BASE_URL = "/api/availability";

  /** Availability endpoints. */
  public static final class Availability {
    public static final String BY_ROOM_ID = "/{roomId}";
    public static final String BY_ROOM_ID_BLOCK = "/{roomId}/block";
    public static final String ROOM_RESERVE = "/{roomId}/reserve";
    public static final String ROOM_RELEASE = "/{roomId}/release";
  }

  /** Catalog endpoints. */
  public static final class Catalog {
    public static final String ALL_ROOMS_BY_HOTEL_ID = "/api/hotels/{hotelId}/rooms";
    public static final String ROOM_BY_ID = "/api/hotels/{hotelId}/rooms/{roomId}";
  }
}
