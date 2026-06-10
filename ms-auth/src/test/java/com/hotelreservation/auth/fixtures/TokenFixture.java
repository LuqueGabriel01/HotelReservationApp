package com.hotelreservation.auth.fixtures;

import com.hotelreservation.auth.enums.TokenType;
import com.hotelreservation.auth.models.entities.Token;
import com.hotelreservation.auth.models.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class providing pre-configured {@link Token} entity fixtures for testing purposes.
 *
 * <p>This class implements the Object Mother pattern to simplify test data setup by providing
 * reusable factory methods for generating various states of token entities (active, inactive,
 * refresh, and custom values) associated with a {@link User}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenFixture {

  public static final String DEFAULT_ACCESS_TOKEN = "access.token.sample";
  public static final String DEFAULT_REFRESH_TOKEN = "refresh.token.sample";

  /**
   * Builds an active, unexpired, and unrevoked {@link TokenType#BEARER} token using the default
   * sample access token value.
   *
   * @param user the {@link User} owner to be associated with the token
   * @return a valid active access {@link Token} instance
   */
  public static Token buildActiveAccessToken(User user) {
    return Token.builder()
        .token(DEFAULT_ACCESS_TOKEN)
        .tokenType(TokenType.BEARER)
        .user(user)
        .expired(false)
        .revoked(false)
        .build();
  }

  /**
   * Builds an active, unexpired, and unrevoked {@link TokenType#REFRESH} token using the default
   * sample refresh token value.
   *
   * @param user the {@link User} owner to be associated with the token
   * @return a valid active refresh {@link Token} instance
   */
  public static Token buildActiveRefresh(User user) {
    return Token.builder()
        .token(DEFAULT_REFRESH_TOKEN)
        .tokenType(TokenType.REFRESH)
        .user(user)
        .expired(false)
        .revoked(false)
        .build();
  }

  /**
   * Builds an inactive {@link TokenType#BEARER} token that is marked as both expired and revoked.
   *
   * @param user the {@link User} owner to be associated with the token
   * @return an invalid inactive {@link Token} instance
   */
  public static Token buildInactive(User user) {
    return Token.builder()
        .token("inactive.token.sample")
        .tokenType(TokenType.BEARER)
        .user(user)
        .expired(true)
        .revoked(true)
        .build();
  }

  /**
   * Builds an active, unexpired, and unrevoked {@link TokenType#BEARER} token with a custom token
   * string value.
   *
   * @param user the {@link User} owner to be associated with the token
   * @param tokenValue the custom string value representing the token payload
   * @return a valid active access {@link Token} instance with the specified custom value
   */
  public static Token buildActiveAccessWithValue(User user, String tokenValue) {
    return Token.builder()
        .token(tokenValue)
        .tokenType(TokenType.BEARER)
        .user(user)
        .expired(false)
        .revoked(false)
        .build();
  }

  /**
   * Parses and validates a signed JSON Web Token (JWT) using the provided secret key to extract its
   * payload claims.
   *
   * @param secretKey the {@link SecretKey} used to cryptographically verify the token's signature
   * @param token the signed JWT string payload to be parsed
   * @return the {@link Claims} instance containing the claims extracted from the token payload
   */
  public static Claims parserJwt(SecretKey secretKey, String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }
}
