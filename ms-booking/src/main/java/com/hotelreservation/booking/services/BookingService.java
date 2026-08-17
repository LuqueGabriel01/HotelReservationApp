package com.hotelreservation.booking.services;

import com.hotelreservation.booking.client.AuthClient;
import com.hotelreservation.booking.client.AvailabilityClient;
import com.hotelreservation.booking.client.CatalogClient;
import com.hotelreservation.booking.constants.ConfigConstants;
import com.hotelreservation.booking.constants.ErrorConstants;
import com.hotelreservation.booking.constants.HeaderConstants;
import com.hotelreservation.booking.exceptions.BadRequestException;
import com.hotelreservation.booking.exceptions.ConflictException;
import com.hotelreservation.booking.exceptions.ForbiddenException;
import com.hotelreservation.booking.exceptions.NotFoundException;
import com.hotelreservation.booking.mappers.BookingMapper;
import com.hotelreservation.booking.models.dtos.event.BookingCancelPayload;
import com.hotelreservation.booking.models.dtos.event.BookingConfirmedPayload;
import com.hotelreservation.booking.models.dtos.internal.InternalAuthInfo;
import com.hotelreservation.booking.models.dtos.internal.InternalCatalogInfo;
import com.hotelreservation.booking.models.dtos.request.CreateBookingRequest;
import com.hotelreservation.booking.models.dtos.response.BookingAdminSummaryResponse;
import com.hotelreservation.booking.models.dtos.response.BookingDeleteResponse;
import com.hotelreservation.booking.models.dtos.response.BookingDetailResponse;
import com.hotelreservation.booking.models.dtos.response.BookingHotelSummaryResponse;
import com.hotelreservation.booking.models.dtos.response.BookingPageResponse;
import com.hotelreservation.booking.models.dtos.response.BookingResponse;
import com.hotelreservation.booking.models.dtos.response.BookingSummaryResponse;
import com.hotelreservation.booking.models.entities.Booking;
import com.hotelreservation.booking.models.entities.History;
import com.hotelreservation.booking.models.enums.BookingStatus;
import com.hotelreservation.booking.repositories.BookingRepository;
import com.hotelreservation.booking.repositories.HistoryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service handling core booking business logic, management, and event publishing. */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

  private final BookingRepository bookingRepository;
  private final HistoryRepository historyRepository;
  private final AuthClient authClient;
  private final CatalogClient catalogClient;
  private final AvailabilityClient availabilityClient;
  private final BookingMapper mapper;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  /**
   * Creates a new booking, validates availability, confirms lock, and publishes confirmation event.
   *
   * @param request the request containing booking details
   * @param userId the ID of the user creating the booking
   * @return the confirmed booking response payload
   * @throws ConflictException if dates overlap or lock is expired/invalid
   */
  @Transactional
  public BookingResponse createBooking(CreateBookingRequest request, UUID userId) {

    InternalCatalogInfo catalogInfo =
        catalogClient.getInternalCatalogInfo(request.hotelId(), request.roomId());

    if (bookingRepository.existsOverlappingBooking(
        request.roomId(), request.checkIn(), request.checkOut())) {
      throw new ConflictException(ErrorConstants.Message.BOOKING_ALREADY_EXISTS);
    }

    long nights = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());

    BigDecimal totalPrice = catalogInfo.pricePerNight().multiply(BigDecimal.valueOf(nights));

    Booking booking =
        Booking.builder()
            .userId(userId)
            .hotelId(request.hotelId())
            .hotelName(catalogInfo.hotelName())
            .hotelAddress(catalogInfo.hotelAddress())
            .roomId(request.roomId())
            .roomType(catalogInfo.type())
            .checkIn(request.checkIn())
            .checkOut(request.checkOut())
            .nights(Math.toIntExact(nights))
            .pricePerNight(catalogInfo.pricePerNight())
            .totalPrice(totalPrice)
            .status(BookingStatus.PENDING)
            .build();

    Booking saved = bookingRepository.save(booking);

    try {
      History history =
          History.builder()
              .booking(saved)
              .previousStatus(null)
              .newStatus(BookingStatus.CONFIRMED)
              .reason(null)
              .build();

      saved.statusConfirmed();
      bookingRepository.save(saved);
      historyRepository.save(history);

      InternalAuthInfo user = authClient.getAuthInfo(userId);

      BookingConfirmedPayload confirmedPayload = mapper.toConfirmedPayload(saved, user);
      availabilityClient.getInternalAvailabilityInfo(
          request.roomId(), request.lockId(), saved.getId());
      kafkaTemplate.send(ConfigConstants.KafkaTopics.BOOKING_CONFIRMED, confirmedPayload);
    } catch (NotFoundException e) {
      bookingRepository.delete(saved);
      throw new ConflictException(ErrorConstants.Message.BOOKING_EXPIRED);
    }
    return mapper.toBookingResponse(saved);
  }

  /**
   * Retrieves detailed information for a specific booking.
   *
   * @param id the unique booking identifier
   * @param userId the ID of the requesting user
   * @param role the role of the requesting user
   * @return the detailed booking information
   * @throws NotFoundException if the booking does not exist
   * @throws ForbiddenException if a user tries to access another user's booking
   */
  @Transactional(readOnly = true)
  public BookingDetailResponse getBookingDetail(UUID id, UUID userId, String role) {

    Booking booking =
        bookingRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorConstants.Message.BOOKING_NOT_FOUND));

    if (role.equals(HeaderConstants.ROLE_USER) && !booking.getUserId().equals(userId)) {
      throw new ForbiddenException(ErrorConstants.Message.BOOKING_NOT_USER);
    }

    return mapper.toBookingDetails(booking);
  }

  /**
   * Retrieves a paginated list of bookings belonging to the current user.
   *
   * @param userId the unique identifier of the user
   * @param status optional status filter
   * @param pageable pagination parameters
   * @return a paginated summary of user bookings
   */
  @Transactional(readOnly = true)
  public BookingPageResponse<BookingSummaryResponse> getMyBookings(
      UUID userId, BookingStatus status, Pageable pageable) {

    Page<Booking> bookingPage = bookingRepository.findByUserIdAndStatus(userId, status, pageable);

    return mapper.toSummaryPageResponse(bookingPage);
  }

  /**
   * Cancels an existing booking and releases availability locks.
   *
   * @param id the unique booking identifier
   * @param userId the ID of the requesting user
   * @param role the role of the requesting user
   * @return the cancellation response details
   * @throws NotFoundException if the booking does not exist
   * @throws ForbiddenException if a non-owner user attempts deletion
   * @throws BadRequestException if the booking is already cancelled
   */
  @Transactional
  public BookingDeleteResponse deleteBookingById(UUID id, UUID userId, String role) {

    Booking booking =
        bookingRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorConstants.Message.BOOKING_NOT_FOUND));

    if (!booking.getUserId().equals(userId) && role.equals(HeaderConstants.ROLE_USER)) {
      throw new ForbiddenException(ErrorConstants.Message.BOOKING_NOT_USER);
    }

    if (booking.getStatus().equals(BookingStatus.CANCELLED)) {
      throw new BadRequestException(ErrorConstants.Message.BOOKING_CAN_NOT_CANCELLED);
    }

    availabilityClient.getInternalAvailabilityRelease(booking.getRoomId(), booking.getId());

    BookingStatus previousStatus = booking.getStatus();

    booking.statusCancelled();

    Booking saved = bookingRepository.save(booking);

    History history =
        History.builder()
            .booking(saved)
            .previousStatus(previousStatus)
            .newStatus(booking.getStatus())
            .reason(null)
            .build();

    historyRepository.save(history);

    BookingDeleteResponse response =
        new BookingDeleteResponse(booking.getId(), booking.getStatus(), Instant.now());

    InternalAuthInfo user = authClient.getAuthInfo(userId);

    BookingCancelPayload cancelPayload = mapper.toCancelPayload(booking, user);

    kafkaTemplate.send(ConfigConstants.KafkaTopics.BOOKING_CANCELLED, cancelPayload);

    return response;
  }

  /**
   * Retrieves a paginated list of all system bookings for administrative audit.
   *
   * @param role the role of the requesting user
   * @param pageable pagination parameters
   * @return a paginated summary list of all bookings
   * @throws ForbiddenException if the user is not an admin
   */
  @Transactional(readOnly = true)
  public BookingPageResponse<BookingAdminSummaryResponse> getBookings(
      String role, Pageable pageable) {

    if (!role.equals(HeaderConstants.ROLE_ADMIN)) {
      throw new ForbiddenException(ErrorConstants.Message.BOOKING_FORBIDDEN);
    }

    Page<Booking> bookings = bookingRepository.findAll(pageable);

    return mapper.toAdminSummaryPageResponse(bookings);
  }

  /**
   * Retrieves a paginated list of bookings associated with a specific hotel.
   *
   * @param hotelId the unique identifier of the hotel
   * @param role the role of the requesting user
   * @param pageable pagination parameters
   * @return a paginated hotel-specific booking summary list
   * @throws ForbiddenException if the user is not an admin
   */
  @Transactional(readOnly = true)
  public BookingPageResponse<BookingHotelSummaryResponse> getBookingsForIdHotel(
      UUID hotelId, String role, Pageable pageable) {

    if (!role.equals(HeaderConstants.ROLE_ADMIN)) {
      throw new ForbiddenException(ErrorConstants.Message.BOOKING_FORBIDDEN);
    }

    Page<Booking> bookings = bookingRepository.findAllByHotelId(hotelId, pageable);

    return mapper.toHotelSummaryPageResponse(bookings);
  }
}
