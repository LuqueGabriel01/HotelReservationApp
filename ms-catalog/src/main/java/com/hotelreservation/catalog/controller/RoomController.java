package com.hotelreservation.catalog.controller;

import com.hotelreservation.catalog.constants.ApiPaths;
import com.hotelreservation.catalog.constants.OpenApiConstants;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Room controller",
        description = "Endpoints for room persistence"
)
@RequestMapping(ApiPaths.Hotel.BASE_URL)
public class RoomController {

    private final RoomService roomService;

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
                    content =
                    @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.NOT_FOUND,
                    description = "Hotel with that id not found.",
                    content =
                    @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(ApiPaths.Room.ROOMS)
    public ResponseEntity<List<RoomResponseDto>> getAllRooms(
            @Parameter(description = "Hotel UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID hotelId,

            @Parameter(description = "Filter room by type", example = "single")
            @RequestParam(required = false) String type,

            @Parameter(description = "Filter room by capacity", example = "2")
            @RequestParam(required = false) Integer capacity,

            @Parameter(description = "Filter room by min price", example = "0")
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Filter room by max price", example = "1000")
            @RequestParam(required = false) BigDecimal maxPrice
            ){

        RoomFilterRequest filterRequest = new RoomFilterRequest(type, capacity, minPrice, maxPrice);
        return ResponseEntity.status(HttpStatus.OK).body(roomService.findAllRooms(filterRequest, hotelId));
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
                    content =
                    @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(ApiPaths.Room.ROOM_BY_ID)
    public ResponseEntity<RoomResponseDto> getRoomById(
            @Parameter(description = "Hotel UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID hotelId,

            @Parameter(description = "Room UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID roomId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(roomService.findRoomById(hotelId, roomId));
    }

    @Operation(
            summary = "Create a new room",
            description =
                    """
                            Creates a new room in the system.
                            ADMIN only.
                            """
    )
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
            @Parameter(description = "Hotel UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID hotelId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Room data to create",
                    required = true,
                    content =
                            @Content(schema = @Schema(implementation = CreateRoomRequestDto.class))
            )
            @RequestBody @Valid CreateRoomRequestDto newRoom
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(newRoom, hotelId));
    }

    @Operation(
            summary = "Update an existing room",
            description =
                    """
                            Updates an existing room in the system.
                            Not all the fields required.
                            ADMIN only.
                            """
    )
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
                    content =
                            @Content(schema = @Schema(implementation = UpdateRoomRequestDto.class))
            )
            @RequestBody UpdateRoomRequestDto updatedRoom,

            @Parameter(description = "Hotel UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID hotelId,

            @Parameter(description = "Room UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID roomId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(roomService.updateRoom(updatedRoom, hotelId, roomId));
    }
    @Operation(
            summary = "Deletes a room from the system",
            description =
                    """
                            Deletes a room from the system.
                            ADMIN only.
                            """
    )
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

            @Parameter(description = "Hotel UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID hotelId,

            @Parameter(description = "Room UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID roomId
    ){
        roomService.deleteRoom(hotelId, roomId);
        return ResponseEntity.noContent().build();
    }
}
