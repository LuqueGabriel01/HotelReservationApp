package com.hotelreservation.catalog.controller;

import com.hotelreservation.catalog.constants.ApiPaths;
import com.hotelreservation.catalog.constants.OpenApiConstants;
import com.hotelreservation.catalog.models.dto.external.BookingRequestDto;
import com.hotelreservation.catalog.models.dto.request.room.CreateRoomRequestDto;
import com.hotelreservation.catalog.models.dto.request.room.RoomFilterRequest;
import com.hotelreservation.catalog.models.dto.request.room.UpdateRoomRequestDto;
import com.hotelreservation.catalog.models.dto.response.ErrorResponse;
import com.hotelreservation.catalog.models.dto.response.room.RoomResponseDto;
import com.hotelreservation.catalog.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST controller managing hotel room inventory, filtering queries, and modifications. */
@RestController
@RequiredArgsConstructor
@Tag(name = "Room controller", description = "Endpoints for room persistence")
@RequestMapping(ApiPaths.Hotel.BASE_URL)
public class RoomController {

  private final RoomService roomService;

  /**
   * Retrieves a list of rooms matching specified criteria inside a target hotel.
   *
   * @param hotelId unique UUID identifier of the parent hotel
   * @param type optional room category layout filter type
   * @param capacity optional room guest limit capacity filter
   * @param minPrice optional minimal room price threshold filter
   * @param maxPrice optional maximum room price threshold filter
   * @return response entity containing the filtered list of room details
   */
  @Operation(
      summary = "Get all rooms",
      description =
          """
                            Get all the rooms with their respective data with the hotel id.
                            Filters by type, capacity and price.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "All rooms fetched correctly.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomResponseDto.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Invalid filter parameters.",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel with that id not found.",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @GetMapping(ApiPaths.Room.ROOMS)
  public ResponseEntity<List<RoomResponseDto>> getAllRooms(
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID hotelId,
      @Parameter(description = "Filter room by type", example = "single")
          @RequestParam(required = false)
          String type,
      @Parameter(description = "Filter room by capacity", example = "2")
          @RequestParam(required = false)
          Integer capacity,
      @Parameter(description = "Filter room by min price", example = "0")
          @RequestParam(required = false)
          BigDecimal minPrice,
      @Parameter(description = "Filter room by max price", example = "1000")
          @RequestParam(required = false)
          BigDecimal maxPrice) {

    RoomFilterRequest filterRequest = new RoomFilterRequest(type, capacity, minPrice, maxPrice);
    return ResponseEntity.status(HttpStatus.OK)
        .body(roomService.findAllRooms(filterRequest, hotelId));
  }

  @Operation(
      summary = "Get a room by their id",
      description =
          """
                            Checks if the room and hotel exist and fetches it
                            by their uuid.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Room fetched correctly.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomResponseDto.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel or room with that id not found.",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @GetMapping(ApiPaths.Room.ROOM_BY_ID)
  public ResponseEntity<RoomResponseDto> getRoomById(
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID hotelId,
      @Parameter(
              description = OpenApiConstants.Example.ROOM_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID roomId) {
    return ResponseEntity.status(HttpStatus.OK).body(roomService.findRoomById(hotelId, roomId));
  }

  @Operation(
      summary = "Create a new room",
      description =
          """
                            Creates a new room in the system.
                            ADMIN only.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.CREATED,
        description = "Room created successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomResponseDto.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.CONFLICT,
        description = "Room already exists.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel not found.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Invalid request body",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(ApiPaths.Room.ROOMS)
  public ResponseEntity<RoomResponseDto> createRoom(
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID hotelId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Room data to create",
              required = true,
              content = @Content(schema = @Schema(implementation = CreateRoomRequestDto.class)))
          @RequestBody
          @Valid
          CreateRoomRequestDto newRoom) {
    return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(newRoom, hotelId));
  }

  @Operation(
      summary = "Update an existing room",
      description =
          """
                            Updates an existing room in the system.
                            Not all the fields required.
                            ADMIN only.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Room updated successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomResponseDto.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel or room not found.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Invalid request body",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping(ApiPaths.Room.ROOM_BY_ID)
  public ResponseEntity<RoomResponseDto> updateRoom(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Room data to update",
              required = true,
              content = @Content(schema = @Schema(implementation = UpdateRoomRequestDto.class)))
          @RequestBody
          UpdateRoomRequestDto updatedRoom,
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID hotelId,
      @Parameter(
              description = OpenApiConstants.Example.ROOM_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID roomId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(roomService.updateRoom(updatedRoom, hotelId, roomId));
  }

  @Operation(
      summary = "Deletes a room from the system",
      description =
          """
                            Deletes a room from the system.
                            ADMIN only.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NO_CONTENT,
        description = "Room deleted successfully."),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel or room not found.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Invalid UUID",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping(ApiPaths.Room.ROOM_BY_ID)
  public ResponseEntity<Void> deleteRoom(
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID hotelId,
      @Parameter(
              description = OpenApiConstants.Example.ROOM_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID roomId) {
    roomService.deleteRoom(hotelId, roomId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Get hotel and room info for booking proccess",
      description =
          """
                            Get the data from the hotel and the room.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Hotel and room data fetched correctly.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingRequestDto.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel or room with that id not found.",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @GetMapping(ApiPaths.Booking.ROOM_BY_ID_BOOKING)
  public ResponseEntity<BookingRequestDto> getRoomBookingInfo(
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID hotelId,
      @Parameter(
              description = OpenApiConstants.Example.ROOM_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID roomId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(roomService.getRoomBookingInfo(hotelId, roomId));
  }
}
