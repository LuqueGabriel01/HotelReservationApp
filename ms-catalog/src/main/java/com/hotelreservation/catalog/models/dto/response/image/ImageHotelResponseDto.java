package com.hotelreservation.catalog.models.dto.response.image;

import java.util.UUID;

public record ImageHotelResponseDto (

    UUID id,
    String url,
    boolean isMain
){}


