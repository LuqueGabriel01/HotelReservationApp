package com.hotelreservation.availability.controllers;

import com.hotelreservation.availability.constants.ApiPaths;
import com.hotelreservation.availability.constants.HeaderConstants;
import com.hotelreservation.availability.constants.OpenApiConstants;
import com.hotelreservation.availability.models.dtos.request.DateRangeRequestDto;
import com.hotelreservation.availability.models.dtos.response.ErrorResponse;
import com.hotelreservation.availability.models.dtos.response.roomblock.RoomBlockResponseDto;
import com.hotelreservation.availability.services.BlockService;
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

import java.util.UUID;

/** REST controller endpoints managing room blocks. */
@RestController
@RequiredArgsConstructor
@Tag(name = "Room block controller", description = "Endpoints for room block persistence")
@RequestMapping(ApiPaths.BASE_URL)
public class BlockController {

    private final BlockService blockService;

    @Operation(
            summary = "Block an available room temporally between two dates.",
            description =
                    """
                            Block an available room between two date ranges.
                            Checks first if the room is still available,
                            blocks it the required days.
                            Changes the status in AVAILABILITY and then saves the blocks"
                            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.SUCCESS,
                    description = "Room blocked successfully.",
                    content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RoomBlockResponseDto.class))),
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
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.SERVICE_UNAVAILABLE,
                    description = "The other services are not available.",
                    content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),

    })
    @PostMapping(ApiPaths.Availability.BY_ROOM_ID_BLOCK)
    public ResponseEntity<RoomBlockResponseDto> blockRoom(

            @Parameter(
                    description = OpenApiConstants.Example.ROOM_UUID,
                    example = OpenApiConstants.Example.EXAMPLE_UUID)
            @PathVariable UUID roomId,

            @Parameter(description = OpenApiConstants.Example.HOTEL_UUID, example = OpenApiConstants.Example.EXAMPLE_UUID)
            @RequestParam UUID hotelId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = OpenApiConstants.Example.DATES,
                    required = true,
                    content = @Content(schema = @Schema(implementation = DateRangeRequestDto.class)))
            @RequestBody @Valid DateRangeRequestDto dates,

            @Parameter(
                    description = "ID of the authenticated user, extracted from JWT by the gateway",
                    example = OpenApiConstants.Example.EXAMPLE_UUID,
                    hidden = true
            )
            @RequestHeader(HeaderConstants.Security.X_USER_ID) UUID userId
            ){
        return ResponseEntity.status(HttpStatus.OK).body(blockService.blockRoom(hotelId, roomId, userId, dates.checkIn(), dates.checkOut()));
    }

    @Operation(
            summary = "Block an available room temporally between two dates.",
            description =
                    """
                            Block an available room between two date ranges.
                            Checks first if the room is still available,
                            blocks it the required days.
                            Changes the status in AVAILABILITY and then saves the blocks"
                            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.NO_CONTENT,
                    description = "Room blocked successfully."),
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.NOT_FOUND,
                    description = "Room block or user not found.",
                    content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.FORBIDDEN,
                    description = "Room block not owned by this user",
                    content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),

    })
    @DeleteMapping(ApiPaths.Availability.BY_ROOM_ID_BLOCK)
    public ResponseEntity<Void> deleteRoomBlock (
            @Parameter(
                    description = OpenApiConstants.Example.ROOM_UUID,
                    example = OpenApiConstants.Example.EXAMPLE_UUID)
            @PathVariable UUID roomId,

            @Parameter(description = OpenApiConstants.Example.LOCK_UUID, example = OpenApiConstants.Example.EXAMPLE_UUID)
            @RequestParam UUID lockId,

            @Parameter(
                    description = "ID of the authenticated user, extracted from JWT by the gateway",
                    example = OpenApiConstants.Example.EXAMPLE_UUID,
                    hidden = true
            )
            @RequestHeader(HeaderConstants.Security.X_USER_ID) UUID userId
    ){

        blockService.deleteRoomBlock(userId, lockId);
        return ResponseEntity.noContent().build();
    }
}
