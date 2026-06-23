package com.hotelreservation.catalog.models.dto.request.hotel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Data transfer object representing the required request payload to register a new hotel. Includes
 * data validation constraints for incoming parameters.
 */
public record CreateHotelRequestDto(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 150) String description,
    @NotBlank @Size(max = 100) String address,
    @NotBlank @Size(max = 50) String city,
    @Min(1) @Max(5) int stars,
    @NotEmpty List<String> amenities) {}
