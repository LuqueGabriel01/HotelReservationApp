package com.hotelreservation.auth.security.handlers;

import com.hotelreservation.auth.constants.ErrorConstants;
import com.hotelreservation.auth.security.writer.ErrorWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Custom authentication entry point to handle unauthenticated requests attempting to access
 * protected resources.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

  private final ErrorWriter writer;

  /** Intercepts authentication failures and writes a standardized unauthorized error response. */
  @Override
  public void commence(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull AuthenticationException authException)
      throws IOException {
    writer.write(response, HttpStatus.UNAUTHORIZED, ErrorConstants.Message.ACCESS_DENIED);
  }
}
