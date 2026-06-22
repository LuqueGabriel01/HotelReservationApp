package com.hotelreservation.catalog.mappers;

import com.hotelreservation.catalog.models.dto.response.image.ImageHotelResponseDto;
import com.hotelreservation.catalog.models.entities.ImageHotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper component responsible for converting ImageHotel domain entities into their
 * corresponding data transfer object response profiles.
 */
@Mapper(componentModel = "spring")
public interface ImageMapper {

  /**
   * Transforms an ImageHotel entity into an ImageHotelResponseDto. Maps the status of the main
   * image flag explicitly onto the destination record field.
   *
   * @param imageHotel The source ImageHotel entity to map.
   * @return The target initialized ImageHotelResponseDto instance, or null if the source is null.
   */
  @Mapping(target = "main", source = "main")
  ImageHotelResponseDto toResponseDto(ImageHotel imageHotel);
}
