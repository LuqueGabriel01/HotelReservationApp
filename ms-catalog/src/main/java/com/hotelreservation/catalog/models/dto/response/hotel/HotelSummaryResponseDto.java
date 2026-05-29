package com.hotelreservation.catalog.models.dto.response.hotel;

import java.util.List;
import java.util.UUID;

public record HotelSummaryResponseDto (

     UUID id,
     String name,
     String description,
     String address,
     String city,
     int stars,
     String mainImage,
     List<String> amenities
){}
