package com.hotelreservation.availability.client;

import com.hotelreservation.availability.constants.ApiPaths;
import com.hotelreservation.availability.models.dtos.external.RoomDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/** WebClient client for interacting with the external Catalog service. */
@Component
@RequiredArgsConstructor
public class CatalogClient {

  private final WebClient catalogWebClient;

  /**
   * Fetches all rooms associated with a specific hotel.
   *
   * @param hotelId The unique ID of the hotel.
   * @return List of rooms for the given hotel.
   */
  public List<RoomDto> getRoomsByHotel(UUID hotelId) {
    return catalogWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(ApiPaths.Catalog.ALL_ROOMS_BY_HOTEL_ID).build(hotelId))
        .retrieve()
        .bodyToFlux(RoomDto.class)
        .collectList()
        .block();
  }

  /**
   * Fetches a specific room by its ID and hotel ID.
   *
   * @param hotelId The unique ID of the hotel.
   * @param roomId The unique ID of the room.
   * @return The requested room details.
   */
  public RoomDto getRoomById(UUID hotelId, UUID roomId) {
    return catalogWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(ApiPaths.Catalog.ROOM_BY_ID).build(hotelId, roomId))
        .retrieve()
        .bodyToMono(RoomDto.class)
        .block();
  }
}
