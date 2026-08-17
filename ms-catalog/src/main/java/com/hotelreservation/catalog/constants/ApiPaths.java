package com.hotelreservation.catalog.constants;

import lombok.NoArgsConstructor;

/** Centralized definition of API endpoint paths. */
@NoArgsConstructor
public final class ApiPaths {

  /** Hotel endpoints. */
  public static final class Hotel {
    public static final String BASE_URL = "/api/hotels";
    public static final String BY_ID = "/{id}";
  }

  /** Room endpoints. */
  public static final class Room {
    public static final String ROOMS = "/{hotelId}/rooms";
    public static final String ROOM_BY_ID = "/{hotelId}/rooms/{roomId}";
  }

  /** Photo endpoints. */
  public static final class Photo {
    public static final String PHOTOS = "/{hotelId}/photos";
    public static final String PHOTO_BY_ID = "/{hotelId}/photos/{imageId}";
    public static final String PHOTO_BY_ID_MAIN = "/{hotelId}/photos/{imageId}/main";
  }

  /** Endpoints for ms-booking. */
  public static final class Booking {
    public static final String ROOM_BY_ID_BOOKING = "/{hotelId}/rooms/{roomId}/booking";
  }
}
