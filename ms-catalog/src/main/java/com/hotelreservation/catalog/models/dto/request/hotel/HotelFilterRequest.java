package com.hotelreservation.catalog.models.dto.request.hotel;

import java.util.List;

/**
 * Data transfer object used to encapsulate search and filtering criteria for query operations.
 */
public record HotelFilterRequest (
    String city,
    Integer stars,
    List<String> amenityNames
){}
