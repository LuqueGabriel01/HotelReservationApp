package com.hotelreservation.catalog.mappers;

import com.hotelreservation.catalog.models.dto.request.room.CreateRoomRequestDto;
import com.hotelreservation.catalog.models.dto.request.room.UpdateRoomRequestDto;
import com.hotelreservation.catalog.models.dto.response.room.RoomResponseDto;
import com.hotelreservation.catalog.models.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper component responsible for converting Room domain entities
 * into data transfer object representation profiles.
 */
@Mapper(componentModel = "spring")
public interface RoomMapper {

    /**
     * Transforms a Room entity into a comprehensive room response DTO.
     * Maps the nested hotel entity identifier directly to the flat hotelId field.
     *
     * @param room The source Room entity to map.
     * @return The target initialized RoomResponseDto instance, or null if the source is null.
     */
    @Mapping(target = "hotelId", source = "hotel.id")
    RoomResponseDto toResponseDto(Room room);
}
