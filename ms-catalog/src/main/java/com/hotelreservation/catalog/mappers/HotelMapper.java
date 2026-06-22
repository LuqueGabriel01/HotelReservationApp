package com.hotelreservation.catalog.mappers;

import com.hotelreservation.catalog.models.dto.response.hotel.CreateHotelResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.HotelDetailResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.HotelSummaryResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.UpdateHotelResponseDto;
import com.hotelreservation.catalog.models.dto.response.image.ImageHotelResponseDto;
import com.hotelreservation.catalog.models.entities.Amenity;
import com.hotelreservation.catalog.models.entities.Hotel;
import com.hotelreservation.catalog.models.entities.ImageHotel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Mapper interface to convert Hotel entities into Hotel DTO responses. */
@Mapper(componentModel = "spring")
public interface HotelMapper {

  /**
   * Maps a Hotel entity to a simplified summary response DTO. Extracts the main image and amenity
   * names using custom helper methods.
   *
   * @param hotel The source Hotel entity
   * @return The populated HotelSummaryResponseDto
   */
  @Mapping(target = "mainImage", expression = "java(getMainImage(hotel))")
  @Mapping(target = "amenities", expression = "java(getAmenityNames(hotel))")
  HotelSummaryResponseDto toSummaryDto(Hotel hotel);

  /**
   * Maps a Hotel entity to a full detailed response DTO. Resolves all associated images and amenity
   * names.
   *
   * @param hotel The source Hotel entity
   * @return The populated HotelDetailResponseDto
   */
  @Mapping(target = "images", expression = "java(getAllImages(hotel))")
  @Mapping(target = "amenities", expression = "java(getAmenityNames(hotel))")
  HotelDetailResponseDto toDetailDto(Hotel hotel);

  /**
   * Maps a recently updated Hotel entity to its update confirmation DTO.
   *
   * @param hotel The source Hotel entity
   * @return The populated UpdateHotelResponseDto
   */
  @Mapping(target = "createdAt", source = "createdAt")
  CreateHotelResponseDto toCreateResponseDto(Hotel hotel);

  /**
   * Helper method to extract the URL of the primary image designated as main.
   *
   * @param hotel The source Hotel entity
   * @return The URL of the main image, or null if not found
   */
  @Mapping(target = "updatedAt", source = "updatedAt")
  @Mapping(target = "amenities", expression = "java(getAmenityNames(hotel))")
  UpdateHotelResponseDto toUpdateResponseDto(Hotel hotel);

  /**
   * Helper method to extract the URL of the primary image designated as main.
   *
   * @param hotel The source Hotel entity
   * @return The URL of the main image, or null if not found
   */
  default String getMainImage(Hotel hotel) {
    return hotel.getImagesHotel().stream()
        .filter(ImageHotel::isMain)
        .map(ImageHotel::getUrl)
        .findFirst()
        .orElse(null);
  }

  /**
   * Helper method to map a list of Amenity entities to their respective name strings.
   *
   * @param hotel The source Hotel entity
   * @return A list of amenity name strings
   */
  default List<String> getAmenityNames(Hotel hotel) {
    return hotel.getAmenities().stream().map(Amenity::getName).toList();
  }

  /**
   * Helper method to transform the hotel's image entities into an image response DTO collection.
   *
   * @param hotel The source Hotel entity
   * @return A list of ImageHotelResponseDto objects
   */
  default List<ImageHotelResponseDto> getAllImages(Hotel hotel) {
    return hotel.getImagesHotel().stream()
        .map(img -> new ImageHotelResponseDto(img.getId(), img.getUrl(), img.isMain()))
        .toList();
  }
}
