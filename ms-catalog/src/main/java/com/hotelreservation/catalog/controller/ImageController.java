package com.hotelreservation.catalog.controller;

import com.hotelreservation.catalog.constants.ApiPaths;
import com.hotelreservation.catalog.constants.OpenApiConstants;
import com.hotelreservation.catalog.models.dto.response.ErrorResponse;
import com.hotelreservation.catalog.models.dto.response.image.ImageHotelResponseDto;
import com.hotelreservation.catalog.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller endpoints managing hotel resource image uploads, deletions, and metadata updates.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Image controller", description = "Endpoints for room persistence")
@RequestMapping(ApiPaths.Hotel.BASE_URL)
public class ImageController {

  private final ImageService imageService;

  @Operation(summary = "Upload hotel photos", description = "Uploads hotel photos. ADMIN only.")
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.CREATED,
        description = "Photos uploaded successfully to Cloudinary"),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel with that id not found.",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Failed to upload the photos to Cloudinary",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(path = ApiPaths.Photo.PHOTOS, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<List<ImageHotelResponseDto>> uploadImages(
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID hotelId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Images to upload",
              required = true,
              content = @Content(schema = @Schema(implementation = MultipartFile.class)))
          @RequestPart
          @Valid
          List<MultipartFile> photos)
      throws IOException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(imageService.uploadImages(hotelId, photos));
  }

  @Operation(
      summary = "Deletes a image from a specific hotel from the system",
      description =
          """
                            Deletes a image from a specific hotel
                            from both the system adn Cloudinary.
                            ADMIN only.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NO_CONTENT,
        description = "Image deleted successfully."),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel or image not found.",
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
  @DeleteMapping(ApiPaths.Photo.PHOTO_BY_ID)
  public ResponseEntity<Void> deleteImage(
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID hotelId,
      @Parameter(
              description = OpenApiConstants.Example.IMAGE_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID imageId) {
    imageService.deleteImage(hotelId, imageId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Deletes a image from a specific hotel from the system",
      description =
          """
                            Deletes a image from a specific hotel
                            from both the system adn Cloudinary.
                            ADMIN only.
                            """)
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Image deleted successfully."),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "Hotel or image not found.",
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
  @PatchMapping(ApiPaths.Photo.PHOTO_BY_ID_MAIN)
  public ResponseEntity<ImageHotelResponseDto> setMainImage(
      @Parameter(
              description = OpenApiConstants.Example.HOTEL_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID hotelId,
      @Parameter(
              description = OpenApiConstants.Example.IMAGE_UUID,
              example = OpenApiConstants.Example.EXAMPLE_UUID)
          @PathVariable
          UUID imageId) {
    return ResponseEntity.status(HttpStatus.OK).body(imageService.setMainImage(hotelId, imageId));
  }
}
