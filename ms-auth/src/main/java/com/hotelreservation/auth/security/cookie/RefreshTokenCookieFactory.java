package com.hotelreservation.auth.security.cookie;

import com.hotelreservation.auth.config.properties.JwtProperties;
import com.hotelreservation.auth.constants.ApiPaths;
import com.hotelreservation.auth.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Factory responsible for building the {@code HttpOnly} cookie that carries the refresh token
 * across the authentication flow.
 *
 * <p>Login, refresh, and logout must all set/clear this cookie using the exact same attributes
 * ({@code Path}, {@code SameSite}, {@code Secure}). Centralizing the construction here guarantees
 * that consistency, since a mismatch in any attribute would prevent the browser from overwriting or
 * deleting a previously set cookie.
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenCookieFactory {

  private final JwtProperties jwtProperties;

  /**
   * Builds the cookie issued after a successful login or token rotation.
   *
   * @param refreshToken the raw refresh token to store in the cookie value
   * @return a {@link ResponseCookie} valid for {@link JwtProperties#getRefreshTokenExpiration()}
   *     seconds
   */
  public ResponseCookie create(String refreshToken) {
    return build(refreshToken, jwtProperties.getRefreshTokenExpiration());
  }

  /**
   * Builds an empty, immediately-expiring cookie used to clear the refresh token on logout.
   *
   * @return a {@link ResponseCookie} with an empty value and {@code Max-Age=0}, so the browser
   *     deletes it upon receiving the response
   */
  public ResponseCookie expire() {
    return build("", 0);
  }

  /**
   * Centralized builder that assembles the cookie with the shared security attributes.
   *
   * @param value the cookie value (the refresh token, or empty when expiring it)
   * @param maxAgeSeconds how long the cookie should live, in seconds ({@code 0} to expire it
   *     immediately)
   * @return a fully configured {@link ResponseCookie}, ready to be added to a {@code Set-Cookie}
   *     response header
   */
  private ResponseCookie build(String value, long maxAgeSeconds) {
    return ResponseCookie.from(SecurityConstants.Field.REFRESH_TOKEN_COOKIE, value)
        .httpOnly(true)
        .secure(true)
        .sameSite(Cookie.SameSite.LAX.attributeValue())
        .path(ApiPaths.Auth.BASE_URL)
        .maxAge(maxAgeSeconds)
        .build();
  }
}
