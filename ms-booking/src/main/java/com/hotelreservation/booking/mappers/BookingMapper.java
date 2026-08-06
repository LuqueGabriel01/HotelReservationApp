package com.hotelreservation.booking.mappers;

import com.hotelreservation.booking.constants.ConfigConstants;
import com.hotelreservation.booking.models.dtos.event.BookingCancelPayload;
import com.hotelreservation.booking.models.dtos.event.BookingConfirmedPayload;
import com.hotelreservation.booking.models.dtos.event.BookingReminderPayload;
import com.hotelreservation.booking.models.dtos.internal.InternalAuthInfo;
import com.hotelreservation.booking.models.dtos.response.BookingAdminSummaryResponse;
import com.hotelreservation.booking.models.dtos.response.BookingDetailResponse;
import com.hotelreservation.booking.models.dtos.response.BookingHotelSummaryResponse;
import com.hotelreservation.booking.models.dtos.response.BookingPageResponse;
import com.hotelreservation.booking.models.dtos.response.BookingResponse;
import com.hotelreservation.booking.models.dtos.response.BookingStatusHistoryResponse;
import com.hotelreservation.booking.models.dtos.response.BookingSummaryResponse;
import com.hotelreservation.booking.models.entities.Booking;
import com.hotelreservation.booking.models.entities.History;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

/** MapStruct mapper for converting between Booking domain entities and DTOs. */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

  BookingResponse toBookingResponse(Booking booking);

  BookingDetailResponse toBookingDetails(Booking booking);

  BookingStatusHistoryResponse toHistoryResponse(History history);

  BookingSummaryResponse toSummaryResponse(Booking booking);

  BookingAdminSummaryResponse toAdminSummaryResponse(Booking booking);

  BookingHotelSummaryResponse toHotelSummaryResponse(Booking booking);

  List<BookingSummaryResponse> toSummaryResponseList(List<Booking> bookings);

  List<BookingAdminSummaryResponse> toAdminSummaryResponseList(List<Booking> bookings);

  List<BookingHotelSummaryResponse> toHotelSummaryResponseList(List<Booking> bookings);

  /**
   * Maps a Spring Data Page of Booking entities to a user summary page response.
   *
   * @param bookingPage the page of booking entities
   * @return the paginated summary response
   */
  default BookingPageResponse<BookingSummaryResponse> toSummaryPageResponse(
      Page<Booking> bookingPage) {
    return new BookingPageResponse<>(
        toSummaryResponseList(bookingPage.getContent()),
        bookingPage.getNumber(),
        bookingPage.getSize(),
        bookingPage.getTotalElements(),
        bookingPage.getTotalPages());
  }

  /**
   * Maps a Spring Data Page of Booking entities to an admin summary page response.
   *
   * @param page the page of booking entities
   * @return the paginated admin summary response
   */
  default BookingPageResponse<BookingAdminSummaryResponse> toAdminSummaryPageResponse(
      Page<Booking> page) {
    return new BookingPageResponse<>(
        toAdminSummaryResponseList(page.getContent()),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  /**
   * Maps a Spring Data Page of Booking entities to a hotel summary page response.
   *
   * @param page the page of booking entities
   * @return the paginated hotel summary response
   */
  default BookingPageResponse<BookingHotelSummaryResponse> toHotelSummaryPageResponse(
      Page<Booking> page) {
    return new BookingPageResponse<>(
        toHotelSummaryResponseList(page.getContent()),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  /**
   * Maps a Booking entity and user info to a booking confirmed event payload.
   *
   * @param booking the booking entity
   * @param user internal user authentication details
   * @return the booking confirmed event payload
   */
  default BookingConfirmedPayload toConfirmedPayload(Booking booking, InternalAuthInfo user) {
    return new BookingConfirmedPayload(
        ConfigConstants.KafkaTopics.BOOKING_CONFIRMED,
        booking.getId(),
        booking.getUserId(),
        user.email(),
        user.username(),
        booking.getHotelId(),
        booking.getHotelName(),
        booking.getRoomType(),
        booking.getCheckIn(),
        booking.getCheckOut(),
        booking.getTotalPrice(),
        null);
  }

  /**
   * Maps a Booking entity and user info to a booking cancel event payload.
   *
   * @param booking the booking entity
   * @param user internal user authentication details
   * @return the booking cancelled event payload
   */
  default BookingCancelPayload toCancelPayload(Booking booking, InternalAuthInfo user) {
    return new BookingCancelPayload(
        ConfigConstants.KafkaTopics.BOOKING_CANCELLED,
        booking.getId(),
        booking.getUserId(),
        user.email(),
        user.username(),
        booking.getHotelName(),
        booking.getRoomType(),
        booking.getCheckIn(),
        booking.getCheckOut(),
        null);
  }

  /**
   * Maps a Booking entity and user info to a booking reminder event payload.
   *
   * @param booking the booking entity
   * @param user internal user authentication details
   * @return the booking reminder event payload
   */
  default BookingReminderPayload toRemindedPayload(Booking booking, InternalAuthInfo user) {
    return new BookingReminderPayload(
        ConfigConstants.KafkaTopics.BOOKING_REMINDER,
        booking.getId(),
        booking.getUserId(),
        user.email(),
        user.username(),
        booking.getHotelName(),
        booking.getHotelAddress(),
        booking.getRoomType(),
        booking.getCheckIn(),
        booking.getCheckOut(),
        null);
  }
}
