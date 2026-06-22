package com.hotelreservation.msgateway.services;

import com.hotelreservation.msgateway.constants.ErrorConstants;
import com.hotelreservation.msgateway.constants.HeaderConstants;
import com.hotelreservation.msgateway.exceptions.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Service responsible for managing JSON Web Tokens (JWT). */
@Service
@RequiredArgsConstructor
public class JwtService {

  private final SecretKey signingKey;

  /** Validates the token signature and expiration. Throws JwtAuthenticationException if invalid. */
  public void validateToken(String token) {
    extractClaims(token);
  }

  /** Extracts the user UUID from the JWT ID claim (jti). */
  public String extractUserId(String token) {
    return extractClaims(token).getId();
  }

  /** Extracts the role from the custom claim. */
  public String extractRole(String token) {
    return extractClaims(token).get(HeaderConstants.Security.ROLE_CLAIMS, String.class);
  }

  /**
   * Parses and validates the token, converting any JJWT exception into a
   * JwtAuthenticationException.
   */
  private Claims extractClaims(String token) {
    try {
      return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    } catch (JwtException e) {
      throw new JwtAuthenticationException(ErrorConstants.Message.INVALID_JWT_TOKEN, e);
    }
  }
}
