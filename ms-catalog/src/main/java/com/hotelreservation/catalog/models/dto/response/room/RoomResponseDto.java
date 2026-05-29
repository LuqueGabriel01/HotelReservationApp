package com.hotelreservation.catalog.models.dto.response.room;

import com.hotelreservation.catalog.enums.RoomType;

import java.math.BigDecimal;
import java.util.UUID;

public record RoomResponseDto(

     UUID id,
     UUID hotelId,
     RoomType type,
     String description,
     int capacity,
     BigDecimal pricePerNight
){}
