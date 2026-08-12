package com.hotelreservation.msnotification.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized field, template, and topic name constants used across the notification service. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FieldsConstant {

  /** Names of the Thymeleaf email templates. */
  public static final class Templates {
    public static final String BOOKING_CONFIRMED = "booking-confirmed";
    public static final String BOOKING_CANCELLED = "booking-cancelled";
    public static final String BOOKING_REMINDER = "booking-reminder";
  }

  /** Names of the Kafka topics consumed by this service. */
  public static final class Topics {
    public static final String BOOKING_CONFIRMED = "booking.confirmed";
    public static final String BOOKING_CANCELLED = "booking.cancelled";
    public static final String BOOKING_REMINDER = "booking.reminder";
    public static final String NOTIFICATIONS_DLQ = "booking.notifications.dlq";
  }

  /** Variable names used in the Thymeleaf template contexts. */
  public static final class Booking {
    public static final String USERNAME = "username";
    public static final String HOTEL_NAME = "hotelName";
    public static final String ROOM_NAME = "roomName";
    public static final String HOTEL_ADDRESS = "hotelAddress";
    public static final String CHECK_IN = "checkIn";
    public static final String CHECK_OUT = "checkOut";
    public static final String TOTAL_PRICE = "totalPrice";
    public static final String BOOKING_ID = "bookingId";
  }
}
