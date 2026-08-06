package com.hotelreservation.booking.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Utility class holding API route paths and endpoints. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiPaths {

  /** Catalog endpoints. */
  public static final class Catalog {
    public static final String HOTELS_ID_ROOMS_ID = "/api/hotels/{hotelId}/rooms/{roomId}/booking";
  }

  /** Availability endpoints. */
  public static final class Availability {
    public static final String AVAILABILITY_ROOM_ID = "/api/availability/{roomId}/reserve";
    public static final String AVAILABILITY_ROOM_ID_RELEASE = "/api/availability/{roomId}/release";
  }

  /** Authentication endpoints. */
  public static final class Auth {
    public static final String AUTH_ME = "/api/auth/internal/{userId}";
  }

  /** Booking endpoints. */
  public static final class Booking {
    public static final String BOOKING = "/api/bookings";
    public static final String BOOKING_ID = "/api/bookings/{id}";
    public static final String BOOKING_MY = "/api/bookings/my";
    public static final String BOOKING_HOTEL_ID = "/api/bookings/hotel/{hotelId}";
  }
}
