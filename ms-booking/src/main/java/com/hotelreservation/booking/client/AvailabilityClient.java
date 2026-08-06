package com.hotelreservation.booking.client;

import com.hotelreservation.booking.constants.ApiPaths;
import com.hotelreservation.booking.models.dtos.internal.InternalAvailabilityInfo;
import com.hotelreservation.booking.models.dtos.internal.InternalAvailabilityRelease;
import com.hotelreservation.booking.models.dtos.internal.ReleaseRoomRequest;
import com.hotelreservation.booking.models.dtos.internal.ReserveRoomRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/** Web client for communicating with the room availability service. */
@Component
@RequiredArgsConstructor
public class AvailabilityClient {

  private final WebClient availabilityWebClient;

  /**
   * Reserves room availability for a booking using a lock ID.
   *
   * @param roomId unique identifier of the room
   * @param lockId unique identifier of the temporary lock
   * @param bookingId unique identifier of the booking
   * @return reserved availability information
   */
  public InternalAvailabilityInfo getInternalAvailabilityInfo(
      UUID roomId, UUID lockId, UUID bookingId) {
    return availabilityWebClient
        .put()
        .uri(
            uriBuilder -> uriBuilder.path(ApiPaths.Availability.AVAILABILITY_ROOM_ID).build(roomId))
        .bodyValue(new ReserveRoomRequest(lockId, bookingId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, WebClientErrorHandler::handleNotFound)
        .onStatus(HttpStatusCode::is5xxServerError, WebClientErrorHandler::handleServerError)
        .bodyToMono(InternalAvailabilityInfo.class)
        .block();
  }

  /**
   * Releases a previously reserved room availability for a booking.
   *
   * @param roomId unique identifier of the room
   * @param bookingId unique identifier of the booking
   * @return release result information
   */
  public InternalAvailabilityRelease getInternalAvailabilityRelease(UUID roomId, UUID bookingId) {
    return availabilityWebClient
        .put()
        .uri(
            uriBuilder ->
                uriBuilder.path(ApiPaths.Availability.AVAILABILITY_ROOM_ID_RELEASE).build(roomId))
        .bodyValue(new ReleaseRoomRequest(bookingId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, WebClientErrorHandler::handleNotFound)
        .onStatus(HttpStatusCode::is5xxServerError, WebClientErrorHandler::handleServerError)
        .bodyToMono(InternalAvailabilityRelease.class)
        .block();
  }
}
