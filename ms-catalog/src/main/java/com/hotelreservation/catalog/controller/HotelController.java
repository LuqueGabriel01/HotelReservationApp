package com.hotelreservation.catalog.controller;

import com.hotelreservation.catalog.constants.ApiPaths;
import com.hotelreservation.catalog.constants.OpenApiConstants;
import com.hotelreservation.catalog.models.dto.request.hotel.CreateHotelRequestDto;
import com.hotelreservation.catalog.models.dto.request.hotel.HotelFilterRequest;
import com.hotelreservation.catalog.models.dto.request.hotel.UpdateHotelRequestDto;
import com.hotelreservation.catalog.models.dto.response.ErrorResponse;
import com.hotelreservation.catalog.models.dto.response.PageResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.CreateHotelResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.HotelDetailResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.HotelSummaryResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.UpdateHotelResponseDto;
import com.hotelreservation.catalog.services.HotelService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Hotel controller",
        description = "Endpoints for hotel persistence "
)
@RequestMapping(ApiPaths.Hotel.BASE_URL)
public class HotelController {

    private final HotelService hotelService;

    @Operation(
            summary = "Get all hotels",
            description =
                    """
                            Get all the hotels with their respective data.
                            Filters by stars, city and amenities and also
                            by page number and size.
                            """)
    @ApiResponses({
            @ApiResponse(
            responseCode = OpenApiConstants.Code.SUCCESS,
            description = "All hotels fetched correctly.",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HotelSummaryResponseDto.class))),
            @ApiResponse(
            responseCode = OpenApiConstants.Code.BAD_REQUEST,
            description = "Invalid pagination or filter parameters.",
            content =
                @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PageResponseDto<HotelSummaryResponseDto>> getAllHotels(
            @Parameter(description = "Filter by city name", example = "Barcelona")
            @RequestParam(required = false) String city,

            @Parameter(description = "Filter by number of stars (1-5)", example = "5")
            @RequestParam(required = false) Integer stars,

            @Parameter(description = "Filter by amenity names", example = "[\"wifi\", \"pool\"]")
            @RequestParam(required = false) List<String> amenityNames,

            @Parameter(description = "Page number (default: 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size (default: 10)", example = "10")
            @RequestParam(defaultValue = "10") int size){

        HotelFilterRequest filterRequest = new HotelFilterRequest(city, stars, amenityNames);
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.findAllHotels(filterRequest, page, size));
    }

    @Operation(
            summary = "Get an hotel by id",
            description =
                    """
                            Checks if the hotel exists and fetches it
                            by their uuid.
                            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.SUCCESS,
                    description = "Hotel fetched successfully.",
                    content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HotelDetailResponseDto.class))),
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.NOT_FOUND,
                    description = "Hotel not found.",
                    content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(ApiPaths.Hotel.BY_ID)
    public ResponseEntity<HotelDetailResponseDto> getHotelById(
            @Parameter(description = "Hotel UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID id
            ){
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.findHotelById(id));
    }

    @Operation(
            summary = "Create a new hotel",
            description =
                    """
                            Creates a new hotel in the system.
                            ADMIN only.
                            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.CREATED,
                    description = "Hotel created successfully.",
                    content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateHotelResponseDto.class))),
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.CONFLICT,
                    description = "Hotel already exists.",
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
    @PostMapping
    public ResponseEntity<CreateHotelResponseDto> createHotel(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Hotel data to create",
                    required = true,
                    content =
                        @Content(schema = @Schema(implementation = CreateHotelRequestDto.class))
            )
            @RequestBody @Valid CreateHotelRequestDto newHotel
            ){
            return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(newHotel));
    }

    @Operation(
            summary = "Update an existing hotel",
            description =
                    """
                            Updates an existing hotel in the system.
                            Not all the fields required.
                            ADMIN only.
                            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.SUCCESS,
                    description = "Hotel updated successfully.",
                    content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateHotelResponseDto.class))),
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
    @PutMapping(ApiPaths.Hotel.BY_ID)
    public ResponseEntity<UpdateHotelResponseDto> updateHotel(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Hotel data to update",
                    required = true,
                    content =
                    @Content(schema = @Schema(implementation = UpdateHotelRequestDto.class))
            )
            @RequestBody UpdateHotelRequestDto updatedHotel,
            @Parameter(description = "Hotel UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID id
            ){
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.updateHotel(updatedHotel, id));
    }

    @Operation(
            summary = "Deletes an hotel from the system",
            description =
                    """
                            Deletes an hotel from the system.
                            ADMIN only.
                            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.NO_CONTENT,
                    description = "Hotel deleted successfully."),
            @ApiResponse(
                    responseCode = OpenApiConstants.Code.NOT_FOUND,
                    description = "Hotel not found.",
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
    @DeleteMapping(ApiPaths.Hotel.BY_ID)
    public ResponseEntity<Void> deleteHotel(
            @Parameter(description = "Hotel UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID id
    ){
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}
