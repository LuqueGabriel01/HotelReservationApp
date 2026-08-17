package com.hotelreservation.msagent.client;

import com.hotelreservation.msagent.constants.ConfigConstants;
import com.hotelreservation.msagent.dto.catalog.RoomResponse;
import com.hotelreservation.msagent.exception.CatalogUnavailableException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/** Client for the room catalog exposed by ms-catalog. */
@Component
public class CatalogClient {

  private final WebClient catalogWebClient;

  public CatalogClient(@Qualifier("catalogWebClient") WebClient catalogWebClient) {
    this.catalogWebClient = catalogWebClient;
  }

  /**
   * Retrieves the rooms available for a hotel, optionally filtered by capacity and maximum price.
   *
   * @param hotelId the identifier of the hotel to query
   * @param capacity the minimum guest capacity to filter by, or {@code null} for no filter
   * @param maxPrice the maximum price per night to filter by, or {@code null} for no filter
   * @return a {@link Mono} emitting the list of matching rooms
   * @throws CatalogUnavailableException if ms-catalog returns an error response or cannot be
   *     reached
   */
  public Mono<List<RoomResponse>> getRoomsByHotel(
      String hotelId, Integer capacity, BigDecimal maxPrice) {
    return catalogWebClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(ConfigConstants.Catalog.ROOMS_BY_HOTEL_PATH)
                    .queryParamIfPresent(
                        ConfigConstants.Catalog.CAPACITY_PARAM, Optional.ofNullable(capacity))
                    .queryParamIfPresent(
                        ConfigConstants.Catalog.MAX_PRICE_PARAM, Optional.ofNullable(maxPrice))
                    .build(hotelId))
        .retrieve()
        .bodyToFlux(RoomResponse.class)
        .collectList()
        .onErrorMap(
            WebClientResponseException.class,
            ex ->
                new CatalogUnavailableException(
                    "ms-catalog responded with an error: " + ex.getStatusCode(), ex))
        .onErrorMap(
            WebClientRequestException.class,
            ex -> new CatalogUnavailableException("Could not reach ms-catalog", ex));
  }
}
