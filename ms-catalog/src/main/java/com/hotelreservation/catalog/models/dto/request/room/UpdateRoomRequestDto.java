package com.hotelreservation.catalog.models.dto.request.room;

import com.hotelreservation.catalog.enums.RoomType;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateRoomRequestDto(

        RoomType type,

        @Size(max = 150)
        String description,

        Integer capacity,

        BigDecimal pricePerNight
){
}
