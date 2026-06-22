package com.hotelreservation.catalog.models.dto.response.image;

import java.util.UUID;

/**
 * Data transfer object representing the metadata and location resource of a hotel image.
 */
public record ImageHotelResponseDto (

    UUID id,
    String url,
    boolean main
){}


