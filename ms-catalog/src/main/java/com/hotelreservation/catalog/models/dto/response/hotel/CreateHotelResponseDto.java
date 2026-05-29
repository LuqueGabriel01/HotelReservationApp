package com.hotelreservation.catalog.models.dto.response.hotel;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateHotelResponseDto(

     UUID ID,
     String name,
     String city,
     int stars,
     LocalDateTime createdAt
){}
