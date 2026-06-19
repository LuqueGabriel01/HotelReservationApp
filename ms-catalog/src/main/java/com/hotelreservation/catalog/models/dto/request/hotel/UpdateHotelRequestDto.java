package com.hotelreservation.catalog.models.dto.request.hotel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Data transfer object representing the request payload to update an existing hotel record.
 * All fields are optional to allow partial updates while maintaining validation limits.
 */
public record UpdateHotelRequestDto (

     @Size(max = 100)
     String name,

     @Size(max = 150)
     String description,

     @Size(max = 100)
     String address,

     @Size(max = 50)
     String city,

     @Min(1)
     @Max(5)
     Integer stars,

     List<String> amenities
){}
