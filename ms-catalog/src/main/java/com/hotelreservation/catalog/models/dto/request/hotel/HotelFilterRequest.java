package com.hotelreservation.catalog.models.dto.request.hotel;

import java.util.List;

public record HotelFilterRequest (
    String city,
    Integer stars,
    List<String> amenityNames
){}
