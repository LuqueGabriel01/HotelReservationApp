package com.hotelreservation.catalog.models.dto.request.hotel;

import jakarta.validation.constraints.*;

import java.util.List;

public record CreateHotelRequestDto (

     @NotBlank
     @Size(max = 100)
     String name,

     @Size(max = 150)
     String description,

     @NotBlank
     @Size(max = 100)
     String address,

     @NotBlank
     @Size(max = 50)
     String city,

     @Min(1)
     @Max(5)
     int stars,

     @NotEmpty
     List<String> amenities
){}
