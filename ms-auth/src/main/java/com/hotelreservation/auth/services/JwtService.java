package com.hotelreservation.auth.services;

import com.hotelreservation.auth.config.properties.JwtProperties;
import com.hotelreservation.auth.constants.SecurityConstants;
import com.hotelreservation.auth.models.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing JSON Web Tokens (JWT).
 *
 * <p>This class handles the creation, validation, and parsing of JWT tokens used for user
 * authentication and authorization within the application.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtProperties jwt;
  private final SecretKey signingKey;

  /**
   * Generates a standard short-lived authentication Access Token for a specific user.
   *
   * @param user the user entity for whom the token is being generated
   * @return a compacted, signed JWT access token string
   */
  public String generateJwtToken(User user) {
    return buildToken(user, jwt.getExpiration());
  }

  /**
   * Generates a long-lived Refresh Token used to request new access tokens without
   * re-authenticating.
   *
   * @param user the user entity for whom the token is being generated
   * @return a compacted, signed JWT refresh token string
   */
  public String generateRefreshToken(User user) {
    return buildToken(user, jwt.getRefreshTokenExpiration());
  }

  /**
   * Extracts the subject (user email) from the provided JWT token payload.
   *
   * <p>If the token signature is invalid, expired, or malformed, the cryptographic exception is
   * caught internally and safely returns null.
   *
   * @param token the raw JWT token string
   * @return the subject/email encrypted inside the token, or null if validation fails
   */
  public String extractUser(String token) {
    try {
      return extractClaims(token).getSubject();
    } catch (JwtException e) {
      return null;
    }
  }

  /**
   * Extracts the expiration date metadata from the provided JWT token payload.
   *
   * <p>If the token parsing fails due to bad structural configurations or signature mismatch, the
   * exception is caught and returns null.
   *
   * @param token the raw JWT token string
   * @return the {@link Date} when this token expires, or null if validation fails
   */
  public Date extractExpiration(String token) {
    try {
      return extractClaims(token).getExpiration();
    } catch (JwtException e) {
      return null;
    }
  }

  /**
   * Verifies the absolute validity of a token against a concrete user context.
   *
   * <p>A token is considered authentic and valid if its extracted username matches the database
   * record's email, its cryptographic signature is intact, and it has not expired.
   *
   * @param token the raw JWT token string to verify
   * @param user the target user entity to cross-reference against the token payload
   * @return true if the token is valid and belongs to the given user; false otherwise
   */
  public boolean isValidToken(String token, User user) {
    String username = extractUser(token);
    return (username != null && username.equals(user.getEmail())) && !isTokenExpired(token);
  }

  /**
   * Decodes the JWT token and extracts all claims using the secure cryptographic signing key.
   *
   * @param token the raw JWT token string
   * @return the claims payload structured as a {@link Claims} instance
   * @throws JwtException if the token cannot be safely parsed, verified, or is untrusted
   */
  private Claims extractClaims(String token) {
    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }

  /**
   * Checks whether the current system time has passed the token's expiration window.
   *
   * @param token the raw JWT token string
   * @return true if the token has expired or is invalid; false if it is still within its validity
   *     window
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Centralized builder to assemble custom claims, structural headers, and signatures into a JWT
   * string.
   *
   * @param user the user domain context containing information to inject into claims
   * @param expiration the validity life span of the token in seconds
   * @return a fully serialized and structurally sound compact JWT string
   */
  private String buildToken(User user, long expiration) {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    return Jwts.builder()
        .id(user.getId().toString())
        .claim(SecurityConstants.Field.USERNAME, user.getUsername())
        .claim(SecurityConstants.Field.ROLE, user.getRole().toString())
        .subject(user.getEmail())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(expiration)))
        .signWith(signingKey)
        .compact();
  }
}
