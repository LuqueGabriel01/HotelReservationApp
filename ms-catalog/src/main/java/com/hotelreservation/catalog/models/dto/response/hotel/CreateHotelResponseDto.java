package com.hotelreservation.catalog.models.dto.response.hotel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data transfer object representing the confirmation response metadata after successfully creating a hotel.
 */
public record CreateHotelResponseDto(

     UUID id,
     String name,
     String city,
     int stars,
     LocalDateTime createdAt
){}
