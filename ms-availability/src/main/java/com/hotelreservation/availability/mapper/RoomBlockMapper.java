package com.hotelreservation.availability.mapper;

import com.hotelreservation.availability.models.dtos.response.roomblock.RoomBlockResponseDto;
import com.hotelreservation.availability.models.entities.RoomBlock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Mapper for room block data. */
@Mapper(componentModel = "spring")
public interface RoomBlockMapper {

  @Mapping(target = "lockId", source = "id")
  @Mapping(target = "expiresAt", expression = "java(roomBlock.getExpiresAt().toInstant())")
  RoomBlockResponseDto toRoomBlockResponseDto(RoomBlock roomBlock);
}
