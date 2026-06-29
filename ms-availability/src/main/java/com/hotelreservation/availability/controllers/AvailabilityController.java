package com.hotelreservation.availability.controllers;

import com.hotelreservation.availability.constants.ApiPaths;
import com.hotelreservation.availability.constants.ErrorConstants;
import com.hotelreservation.availability.constants.OpenApiConstants;
import com.hotelreservation.availability.exception.InvalidDateRangeException;
import com.hotelreservation.availability.models.dtos.response.ErrorResponse;
import com.hotelreservation.availability.models.dtos.response.availability.AvailabilityResponseDto;
import com.hotelreservation.availability.models.dtos.response.availability.AvailabilityRoomResponseDto;
import com.hotelreservation.availability.services.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@Tag(name = "Room availability controller", description = "Endpoints for hotel persistence")
@RequestMapping(ApiPaths.BASE_URL)
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Operation(
            summary = "Get all the available rooms from a specific hotel.",
            description =
                    """
                            Get all the rooms from catalog by hotel id and date range.
                            Check the availabality in those rooms,
                            return only the available ones.
                            """
    )
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
            @Parameter(description = OpenApiConstants.Example.HOTEL_UUID, example = OpenApiConstants.Example.EXAMPLE_UUID)
            @RequestParam(required = true) UUID hotelId,

            @Parameter @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = true) LocalDate checkIn,

            @Parameter @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = true) LocalDate checkOut
            ){

        if (checkIn == null || checkOut == null){
            throw new InvalidDateRangeException(ErrorConstants.Error.DATE_REQUIRED);
        }

        return ResponseEntity.status(HttpStatus.OK).
                body(availabilityService.getAllAvailableRooms(hotelId, checkIn, checkOut));

    }

    @Operation(
            summary = "Get the availability for a specific room for certain dates.",
            description =
                    """
                            Get the a specific room by their id, hotel id and date range.
                            Returns the status and reason if it's RESERVED or BLOCKED.
                            """
    )
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
            @PathVariable UUID roomId,

            @Parameter(description = OpenApiConstants.Example.HOTEL_UUID, example = OpenApiConstants.Example.EXAMPLE_UUID)
            @RequestParam(required = true) UUID hotelId,

            @Parameter @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = true) LocalDate checkIn,

            @Parameter @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = true) LocalDate checkOut
    ){
        if (checkIn == null || checkOut == null){
            throw new InvalidDateRangeException(ErrorConstants.Error.DATE_REQUIRED);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(availabilityService.getRoomAvailabilityById(hotelId, roomId, checkIn, checkOut));
    }
}
