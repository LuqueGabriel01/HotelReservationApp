package com.hotelreservation.catalog.repositories;

import java.math.BigDecimal;
import java.util.UUID;

/** Projection used to aggregate the cheapest room price per hotel. */
public interface HotelMinPriceProjection {

  UUID getHotelId();

  BigDecimal getMinPrice();
}
