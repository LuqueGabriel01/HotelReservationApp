package com.hotelreservation.availability.constants;

import lombok.NoArgsConstructor;

/** Centralized definition of API endpoint paths. */
@NoArgsConstructor
public final class ApiPaths {

  public static  final String BASE_URL = "/api/availibility";

  /** Availability endpoints. */
  public static final class Hotel {
    public static final String BY_ROOM_ID = "/{roomId}";
    public static final String BY_ROOM_ID_BLOCK = "/{roomId}/block";
    public static final String ROOM_RESERVE = "/{roomId}/reserve";
    public static final String ROOM_RELEASE = "/{roomId}/release";
  }

}
