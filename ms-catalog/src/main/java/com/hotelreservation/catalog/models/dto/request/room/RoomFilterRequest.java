package com.hotelreservation.catalog.models.dto.request.room;

import java.math.BigDecimal;

/**
 * Data transfer object used to encapsulate search and filtering criteria for query operations.
 */
public record RoomFilterRequest(
        String type,
        Integer capacity,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {}
