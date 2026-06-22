package com.hotelreservation.catalog.models.dto.response.hotel;

import com.hotelreservation.catalog.models.dto.response.image.ImageHotelResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Data transfer object representing the complete and detailed profile information of a hotel. */
public record HotelDetailResponseDto(
    UUID id,
    String name,
    String description,
    String address,
    String city,
    int stars,
    List<ImageHotelResponseDto> images,
    List<String> amenities,
    LocalDateTime createdAt) {}
