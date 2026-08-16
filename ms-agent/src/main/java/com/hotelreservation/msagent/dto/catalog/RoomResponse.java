package com.hotelreservation.msagent.dto.catalog;

import java.math.BigDecimal;

/** Data transfer object (DTO) representing a hotel room as returned by ms-catalog. */
public record RoomResponse(
    String id,
    String hotelId,
    String type,
    String description,
    Integer capacity,
    BigDecimal pricePerNight) {}
