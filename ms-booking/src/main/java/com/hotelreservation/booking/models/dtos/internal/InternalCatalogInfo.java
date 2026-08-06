package com.hotelreservation.booking.models.dtos.internal;

import java.math.BigDecimal;

/** Internal representation of hotel and room details retrieved from the catalog service. */
public record InternalCatalogInfo(
    String hotelName, String hotelAddress, String type, BigDecimal pricePerNight) {}
