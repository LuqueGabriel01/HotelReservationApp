package com.hotelreservation.booking.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Global configuration constants for the application. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigConstants {

  public static final String BASE_PACKAGE = "com.hotelreservation.booking";

  /** Constants for application property keys. */
  public static final class Properties {
    public static final String HOTEL_SERVICES = "hotel.services";
  }

  /** Constants for Kafka topic names. */
  public static final class KafkaTopics {
    public static final String BOOKING_CANCELLED = "booking.cancelled";
    public static final String BOOKING_CONFIRMED = "booking.confirmed";
    public static final String BOOKING_REMINDER = "booking.reminder";
  }
}
