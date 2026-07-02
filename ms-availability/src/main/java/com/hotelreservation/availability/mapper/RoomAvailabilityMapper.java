package com.hotelreservation.availability.mapper;

import com.hotelreservation.availability.models.dtos.external.RoomDto;
import com.hotelreservation.availability.models.dtos.response.availability.AvailabilityResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Mapper for room availability data. */
@Mapper(componentModel = "spring")
public interface RoomAvailabilityMapper {

  /**
   * Maps room details and availability status to a response DTO.
   *
   * @param roomDto The source room DTO.
   * @param available The availability status.
   * @return The mapped {@link AvailabilityResponseDto}.
   */
  @Mapping(target = "roomId", source = "roomDto.id")
  @Mapping(target = "available", source = "available")
  AvailabilityResponseDto toAvailabilityResponseDto(RoomDto roomDto, boolean available);
}
