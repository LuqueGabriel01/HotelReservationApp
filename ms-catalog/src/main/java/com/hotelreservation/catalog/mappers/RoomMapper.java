package com.hotelreservation.catalog.mappers;

import com.hotelreservation.catalog.models.dto.external.BookingRequestDto;
import com.hotelreservation.catalog.models.dto.response.room.RoomResponseDto;
import com.hotelreservation.catalog.models.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper component responsible for converting Room domain entities into data transfer
 * object representation profiles.
 */
@Mapper(componentModel = "spring")
public interface RoomMapper {

  /**
   * Transforms a Room entity into a comprehensive room response DTO. Maps the nested hotel entity
   * identifier directly to the flat hotelId field.
   *
   * @param room The source Room entity to map.
   * @return The target initialized RoomResponseDto instance, or null if the source is null.
   */
  @Mapping(target = "hotelId", source = "hotel.id")
  RoomResponseDto toResponseDto(Room room);

  /**
   * Maps a {@link Room} entity to a {@link BookingRequestDto}, expanding the embedded hotel
   * properties.
   *
   * @param room the source room entity containing hotel information
   * @return the mapped booking request data transfer object
   */
  @Mapping(target = "hotelId", source = "hotel.id")
  @Mapping(target = "hotelName", source = "hotel.name")
  @Mapping(target = "hotelAddress", source = "hotel.address")
  BookingRequestDto toBookingRequestDto(Room room);
}
