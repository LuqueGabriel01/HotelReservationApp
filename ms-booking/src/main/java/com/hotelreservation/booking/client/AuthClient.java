package com.hotelreservation.booking.client;

import com.hotelreservation.booking.constants.ApiPaths;
import com.hotelreservation.booking.models.dtos.internal.InternalAuthInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/** Web client for communicating with the authentication service. */
@Component
@RequiredArgsConstructor
public class AuthClient {

  /** WebClient instance configured for authentication API requests. */
  public final WebClient authWebClient;

  /**
   * Retrieves internal authentication details for a specific user.
   *
   * @param userId unique identifier of the user
   * @return authentication information for the given user
   */
  public InternalAuthInfo getAuthInfo(UUID userId) {
    return authWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(ApiPaths.Auth.AUTH_ME).build(userId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, WebClientErrorHandler::handleNotFound)
        .onStatus(HttpStatusCode::is5xxServerError, WebClientErrorHandler::handleServerError)
        .bodyToMono(InternalAuthInfo.class)
        .block();
  }
}
