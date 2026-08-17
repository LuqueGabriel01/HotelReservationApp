package com.hotelreservation.booking.client;

import com.hotelreservation.booking.constants.ApiPaths;
import com.hotelreservation.booking.models.dtos.internal.InternalCatalogInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/** Web client for communicating with the catalog service. */
@Component
@RequiredArgsConstructor
public class CatalogClient {

  private final WebClient catalogWebClient;

  /**
   * Retrieves catalog details for a specific room within a hotel.
   *
   * @param hotelId unique identifier of the hotel
   * @param roomId unique identifier of the room
   * @return catalog information for the specified hotel room
   */
  public InternalCatalogInfo getInternalCatalogInfo(UUID hotelId, UUID roomId) {
    return catalogWebClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder.path(ApiPaths.Catalog.HOTELS_ID_ROOMS_ID).build(hotelId, roomId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, WebClientErrorHandler::handleNotFound)
        .onStatus(HttpStatusCode::is5xxServerError, WebClientErrorHandler::handleServerError)
        .bodyToMono(InternalCatalogInfo.class)
        .block();
  }
}
