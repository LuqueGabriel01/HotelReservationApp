package com.hotelreservation.auth.security.handlers;

import com.hotelreservation.auth.constants.ErrorConstants;
import com.hotelreservation.auth.constants.SecurityConstants;
import com.hotelreservation.auth.models.dtos.response.ErrorResponse;
import com.hotelreservation.auth.models.entities.Token;
import com.hotelreservation.auth.repositories.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/** Custom logout handler responsible for invalidating active user sessions in the database. */
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

  private final TokenRepository tokenRepository;
  private final ObjectMapper objectMapper;

  /**
   * Processes the logout request by extracting the JWT token, validating it, and revoking all
   * active tokens associated with the corresponding user.
   */
  @Override
  public void logout(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      Authentication authentication) {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith(SecurityConstants.Field.BEARER)) {
      writeUnauthorized(response, ErrorConstants.Message.MISSING_INVALID_TOKEN);
      return;
    }

    String jwtToken = authHeader.substring(7);
    Token foundToken = tokenRepository.findByToken(jwtToken).orElse(null);

    if (foundToken == null || foundToken.isRevoked() || foundToken.isExpired()) {
      writeUnauthorized(response, ErrorConstants.Message.MISSING_INVALID_TOKEN);
      return;
    }

    tokenRepository.revokeAllActiveByUserId(foundToken.getUser().getId(), Instant.now());
  }

  private void writeUnauthorized(HttpServletResponse response, String message) {
    try {
      ErrorResponse error =
          ErrorResponse.builder()
              .code(HttpStatus.UNAUTHORIZED.value())
              .name(HttpStatus.UNAUTHORIZED.name())
              .description(message)
              .timestamp(Instant.now())
              .build();

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write(objectMapper.writeValueAsString(error));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
