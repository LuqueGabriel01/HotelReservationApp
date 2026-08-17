package com.hotelreservation.booking.controller;

import com.hotelreservation.booking.constants.ApiPaths;
import com.hotelreservation.booking.constants.HeaderConstants;
import com.hotelreservation.booking.constants.OpenApiConstants;
import com.hotelreservation.booking.models.dtos.request.CreateBookingRequest;
import com.hotelreservation.booking.models.dtos.response.BookingAdminSummaryResponse;
import com.hotelreservation.booking.models.dtos.response.BookingDeleteResponse;
import com.hotelreservation.booking.models.dtos.response.BookingDetailResponse;
import com.hotelreservation.booking.models.dtos.response.BookingHotelSummaryResponse;
import com.hotelreservation.booking.models.dtos.response.BookingPageResponse;
import com.hotelreservation.booking.models.dtos.response.BookingResponse;
import com.hotelreservation.booking.models.dtos.response.BookingSummaryResponse;
import com.hotelreservation.booking.models.dtos.response.ErrorResponse;
import com.hotelreservation.booking.models.enums.BookingStatus;
import com.hotelreservation.booking.services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for managing booking operations. */
@RestController
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;

  /**
   * Creates a new booking.
   *
   * @param request information required to create a new booking
   * @param userId unique identifier of the user creating the booking
   * @return response entity containing created booking details
   */
  @Operation(
      summary = "Create a new booking",
      description =
          """
                    Creates a new booking for a room.
                    Validates catalog availability, ensures no date
                    overlapping occurs, updates room lock, and publishes
                    a confirmation event to Kafka.
                    """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Information required to create a new booking.",
      required = true,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CreateBookingRequest.class)))
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.CREATED,
        description = "Booking created successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Invalid request payload format.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel or room catalog information not found.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.CONFLICT,
        description = "Overlapping booking exists or room lock has expired.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(ApiPaths.Booking.BOOKING)
  public ResponseEntity<BookingResponse> createBooking(
      @RequestBody CreateBookingRequest request,
      @RequestHeader(HeaderConstants.X_USER_ID) UUID userId) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookingService.createBooking(request, userId));
  }

  @Operation(
      summary = "Get booking by ID",
      description = "Only accessible by the booking owner or administrators.")
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Booking details retrieved successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingDetailResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.FORBIDDEN,
        description = OpenApiConstants.Message.FORBIDDEN_MESSAGE,
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Booking not found.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping(ApiPaths.Booking.BOOKING_ID)
  public ResponseEntity<BookingDetailResponse> getBookingById(
      @PathVariable UUID id,
      @RequestHeader(HeaderConstants.X_USER_ID) UUID userId,
      @RequestHeader(HeaderConstants.X_USER_ROLE) String role) {

    return ResponseEntity.ok().body(bookingService.getBookingDetail(id, userId, role));
  }

  @Operation(
      summary = "Get current user bookings",
      description =
          """
            Retrieves a paginated list of bookings created by user,
            optionally filtered by status.""")
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "User bookings retrieved successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingPageResponse.class)))
  })
  @GetMapping(ApiPaths.Booking.BOOKING_MY)
  public ResponseEntity<BookingPageResponse<BookingSummaryResponse>> getBookingSummary(
      @RequestHeader(HeaderConstants.X_USER_ID) UUID userId,
      @RequestParam(required = false) BookingStatus status,
      Pageable pageable) {

    return ResponseEntity.ok().body(bookingService.getMyBookings(userId, status, pageable));
  }

  @Operation(
      summary = "Delete or cancel booking",
      description = "Cancels an existing booking, updates history, and emits cancellation event.")
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Booking cancelled successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingDeleteResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Booking cannot be cancelled because it is already cancelled.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.FORBIDDEN,
        description = OpenApiConstants.Message.FORBIDDEN_MESSAGE,
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Booking not found.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping(ApiPaths.Booking.BOOKING_ID)
  public ResponseEntity<BookingDeleteResponse> deleteBooking(
      @PathVariable UUID id,
      @RequestHeader(HeaderConstants.X_USER_ROLE) String role,
      @RequestHeader(HeaderConstants.X_USER_ID) UUID userId) {

    return ResponseEntity.ok().body(bookingService.deleteBookingById(id, userId, role));
  }

  @Operation(
      summary = "Get all bookings (Admin)",
      description =
          """
              Retrieves a paginated list of all system bookings for administrative oversight.
              Requires ADMIN role.""")
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Bookings list retrieved successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingPageResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.FORBIDDEN,
        description = OpenApiConstants.Message.FORBIDDEN_MESSAGE,
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping(ApiPaths.Booking.BOOKING)
  public ResponseEntity<BookingPageResponse<BookingAdminSummaryResponse>> getBookings(
      @RequestHeader(HeaderConstants.X_USER_ROLE) String role, @PageableDefault Pageable pageable) {

    return ResponseEntity.ok().body(bookingService.getBookings(role, pageable));
  }

  @Operation(
      summary = "Get bookings by hotel ID (Admin)",
      description =
          """
                Retrieves a paginated list of bookings registered for a specific hotel.
                Requires ADMIN role.""")
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Hotel bookings retrieved successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingPageResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.FORBIDDEN,
        description = OpenApiConstants.Message.FORBIDDEN_MESSAGE,
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping(ApiPaths.Booking.BOOKING_HOTEL_ID)
  public ResponseEntity<BookingPageResponse<BookingHotelSummaryResponse>> getBookingByHotelId(
      @PathVariable UUID hotelId,
      @RequestHeader(HeaderConstants.X_USER_ROLE) String role,
      Pageable pageable) {

    return ResponseEntity.ok().body(bookingService.getBookingsForIdHotel(hotelId, role, pageable));
  }
}
