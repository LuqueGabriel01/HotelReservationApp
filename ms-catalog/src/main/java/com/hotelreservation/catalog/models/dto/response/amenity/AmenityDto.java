package com.hotelreservation.catalog.models.dto.response.amenity;

import java.util.UUID;

/** Data transfer object representing the core information of a hotel amenity. */
public record AmenityDto(UUID id, String name) {}
