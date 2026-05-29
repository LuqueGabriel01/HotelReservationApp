package com.hotelreservation.catalog.models.dto.request.room;

import com.hotelreservation.catalog.enums.RoomType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateRoomRequestDto (
     @NotNull
     RoomType type,

     @Size(max = 150)
     String description,

     @Min(1)
     int capacity,

     @DecimalMin(value = "0.0", inclusive = false)
     BigDecimal pricePerNight
){
}
