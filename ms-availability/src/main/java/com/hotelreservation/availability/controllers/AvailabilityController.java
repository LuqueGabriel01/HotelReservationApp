package com.hotelreservation.availability.controllers;

import com.hotelreservation.availability.constants.ApiPaths;
import com.hotelreservation.availability.constants.ErrorConstants;
import com.hotelreservation.availability.constants.OpenApiConstants;
import com.hotelreservation.availability.exception.InvalidDateRangeException;
import com.hotelreservation.availability.models.dtos.request.ReleaseRoomRequestDto;
import com.hotelreservation.availability.models.dtos.request.ReserveRoomRequestDto;
import com.hotelreservation.availability.models.dtos.response.ErrorResponse;
import com.hotelreservation.availability.models.dtos.response.availability.AvailabilityResponseDto;
import com.hotelreservation.availability.models.dtos.response.availability.AvailabilityRoomResponseDto;
import com.hotelreservation.availability.models.dtos.response.roomblock.BlockReleaseResponseDto;
import com.hotelreservation.availability.models.dtos.response.roomblock.ReserveRoomResponseDto;
import com.hotelreservation.availability.services.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST controller endpoints managing room availability. */
@RestController
@RequiredArgsConstructor
@Tag(
    name = "Room availability controller",
    description = "Endpoints for room availability persistence")
@RequestMapping(ApiPaths.BASE_URL)
public class AvailabilityController {

  private final AvailabilityService availabilityService;

  /**
   * Endpoint to retrieve available rooms for a given hotel and date range.
   *
   * @param hotelId the unique identifier of the hotel
   * @param checkIn the start date of the filtering window
   * @param checkOut the end date of the filtering window
   * @return a list of available room data transfer objects
   */
  @Operation(
      summary = "Get all the available rooms from a specific hotel.",
      description =
          """
                            Get all the rooms from catalog by hotel id and date range.
                            Check the availability in those rooms,
                            return only the available ones.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Available rooms fetched correctly.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AvailabilityResponseDto.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel not found.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Dates invalid.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping()
  public ResponseEntity<List<AvailabilityResponseDto>> getAvailableRooms(
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @RequestParam
          UUID hotelId,
      @Parameter @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate checkIn,
      @Parameter @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate checkOut) {

    if (checkIn == null || checkOut == null) {
      throw new InvalidDateRangeException(ErrorConstants.Error.DATE_REQUIRED);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(availabilityService.getAllAvailableRooms(hotelId, checkIn, checkOut));
  }

  /**
   * Endpoint to get detailed availability status of a specific room.
   *
   * @param roomId the unique identifier of the room
   * @param hotelId the unique identifier of the hotel
   * @param checkIn the starting date of the check window
   * @param checkOut the ending date of the check window
   * @return the availability details of the requested room
   */
  @Operation(
      summary = "Get the availability for a specific room for certain dates.",
      description =
          """
                            Get the a specific room by their id, hotel id and date range.
                            Returns the status and reason if it's RESERVED or BLOCKED.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Room with its availability fetched correctly.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AvailabilityRoomResponseDto.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel or room not found.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Dates invalid.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping(ApiPaths.Availability.BY_ROOM_ID)
  public ResponseEntity<AvailabilityRoomResponseDto> getRoomAvailability(
      @Parameter(
              description = OpenApiConstants.Example.ROOM_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID roomId,
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @RequestParam
          UUID hotelId,
      @Parameter @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate checkIn,
      @Parameter @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate checkOut) {
    if (checkIn == null || checkOut == null) {
      throw new InvalidDateRangeException(ErrorConstants.Error.DATE_REQUIRED);
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(availabilityService.getRoomAvailabilityById(hotelId, roomId, checkIn, checkOut));
  }

  /**
   * Endpoint to switch a room's status from blocked to a confirmed reservation.
   *
   * @param requestDto details regarding the block and reservation ID
   * @param roomId the unique identifier of the room
   * @return confirmation of the reservation status
   */
  @Operation(
      summary = "Change the availability status and delete the room block.",
      description =
          """
                            First checks the rooms block,
                            deletes all the status BLOCKED in availability.
                            Then saves the new status RESERVED and deletes the room block.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Status changed successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ReserveRoomResponseDto.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Room block not found.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping(ApiPaths.Availability.ROOM_RESERVE)
  public ResponseEntity<ReserveRoomResponseDto> reserveBooking(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Reserve data to change availability status",
              required = true,
              content = @Content(schema = @Schema(implementation = ReserveRoomRequestDto.class)))
          @RequestBody
          @Valid
          ReserveRoomRequestDto requestDto,
      @Parameter(
              description = OpenApiConstants.Example.ROOM_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID roomId) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(availabilityService.reserveBooking(requestDto, roomId));
  }

  /**
   * Endpoint to release and remove a room reservation, making it available again.
   *
   * @param requestDto metadata containing the target booking ID
   * @param roomId the unique identifier of the room
   * @return confirmation of the release action status
   */
  @Operation(
      summary = "Releases the booked rooms.",
      description =
          """
                            Checks all the booked rooms rows and deletes them.
                            They will be available again.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Bookings released successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BlockReleaseResponseDto.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Room block not found.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping(ApiPaths.Availability.ROOM_RELEASE)
  public ResponseEntity<BlockReleaseResponseDto> releaseBooking(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Data to release the booking.",
              required = true,
              content = @Content(schema = @Schema(implementation = ReleaseRoomRequestDto.class)))
          @RequestBody
          @Valid
          ReleaseRoomRequestDto requestDto,
      @Parameter(
              description = OpenApiConstants.Example.ROOM_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID roomId) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(availabilityService.releaseBooking(requestDto, roomId));
  }
}
