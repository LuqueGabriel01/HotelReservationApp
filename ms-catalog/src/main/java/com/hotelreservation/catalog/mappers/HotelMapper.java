package com.hotelreservation.catalog.mappers;

import com.hotelreservation.catalog.models.dto.request.hotel.CreateHotelRequestDto;
import com.hotelreservation.catalog.models.dto.response.hotel.CreateHotelResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.HotelDetailResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.HotelSummaryResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.UpdateHotelResponseDto;
import com.hotelreservation.catalog.models.dto.response.image.ImageHotelResponseDto;
import com.hotelreservation.catalog.models.entities.Amenity;
import com.hotelreservation.catalog.models.entities.Hotel;
import com.hotelreservation.catalog.models.entities.ImageHotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface to convert Hotel entities into Hotel DTO responses.
 */
@Mapper(componentModel = "spring")
public interface HotelMapper {

    // -- Summary --
    @Mapping(target = "mainImage", expression = "java(getMainImage(hotel))")
    @Mapping(target = "amenities", expression = "java(getAmenityNames(hotel))")
    HotelSummaryResponseDto toSummaryDto(Hotel hotel);

    // -- Detail --
    @Mapping(target = "images", expression = "java(getAllImages(hotel))")
    @Mapping(target = "amenities", expression = "java(getAmenityNames(hotel))")
    HotelDetailResponseDto toDetailDto(Hotel hotel);

    // -- Create --
    @Mapping(target = "createdAt", source = "createdAt")
    CreateHotelResponseDto toCreateResponseDto(Hotel hotel);

    // -- Update --
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "amenities", expression = "java(getAmenityNames(hotel))")
    UpdateHotelResponseDto toUpdateResponseDto(Hotel hotel);

    // -- Helpers --
    default String getMainImage(Hotel hotel){
        return hotel.getImagesHotel().stream()
                .filter(ImageHotel::isMain)
                .map(ImageHotel::getUrl)
                .findFirst()
                .orElse(null);

    }

    default List<String> getAmenityNames(Hotel hotel){
        return hotel.getAmenities().stream()
                .map(Amenity::getName)
                .toList();
    }

    default List<ImageHotelResponseDto> getAllImages(Hotel hotel){
        return hotel.getImagesHotel().stream()
                .map(img -> new ImageHotelResponseDto(img.getId(), img.getUrl(), img.isMain()))
                .toList();
    }
}
