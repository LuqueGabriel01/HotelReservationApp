package com.hotelreservation.auth.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotelreservation.auth.config.properties.JwtProperties;
import com.hotelreservation.auth.fixtures.TokenFixture;
import com.hotelreservation.auth.fixtures.UserFixture;
import com.hotelreservation.auth.models.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

  @Mock private JwtProperties jwtProperties;

  private JwtService jwtService;

  private SecretKey secretKey;

  @BeforeEach
  void setUp() {
    secretKey = Jwts.SIG.HS256.key().build();
    jwtService = new JwtService(jwtProperties, secretKey);
  }

  @Nested
  @DisplayName("Tests for generateJwtToken()")
  class GenerateJwtToken {

    @Test
    @DisplayName("should return a non null and non blank token")
    void shouldReturnNonNullNonBlackToken() {

      User user = UserFixture.buildDefaultUser();

      Mockito.when(jwtProperties.getExpiration()).thenReturn(3600L);

      String token = jwtService.generateJwtToken(user);

      assertThat(token).isNotBlank();

      Claims claims = TokenFixture.parserJwt(secretKey, token);

      assertThat(claims.getSubject()).isEqualTo(user.getEmail());
      assertThat(claims.get("username")).isEqualTo(user.getUsername());
      assertThat(claims.get("role")).isEqualTo(user.getRole().toString());
      assertThat(claims.getId()).isEqualTo(user.getId().toString());
    }
  }

  @Nested
  @DisplayName("test for generateResfreshToken()")
  class GenerateRefreshToken {

    @Test
    @DisplayName("should return a different date")
    void shouldReturnRefreshToken() {
      User user = UserFixture.buildDefaultUser();

      Mockito.when(jwtProperties.getRefreshTokenExpiration()).thenReturn(604800L);
      Mockito.when(jwtProperties.getExpiration()).thenReturn(3600L);

      String token = jwtService.generateJwtToken(user);
      String refreshToken = jwtService.generateRefreshToken(user);

      Date tokenDate = jwtService.extractExpiration(token);
      Date refreshDate = jwtService.extractExpiration(refreshToken);

      assertThat(tokenDate).isNotNull();
      assertThat(refreshDate).isNotNull();
      assertThat(tokenDate).isNotEqualTo(refreshDate);
    }
  }

  @Nested
  @DisplayName("test for extractUser()")
  class ExtractUser {
    @Test
    @DisplayName("should extract the email address correctly")
    void shouldReturnWhenEmailIsCorrectly() {
      User user = UserFixture.buildDefaultUser();

      Mockito.when(jwtProperties.getExpiration()).thenReturn(3600L);

      String token = jwtService.generateJwtToken(user);

      Claims claims = TokenFixture.parserJwt(secretKey, token);

      assertThat(token).isNotBlank();
      assertThat(claims.getSubject()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("should return null when token is malformed or invalid")
    void shouldReturnNullWhenTokenIsInvalid() {

      String token = "fake.token";

      String resultUser = jwtService.extractUser(token);

      assertThat(resultUser).isNull();
    }
  }

  @Nested
  @DisplayName("test for isValidToken()")
  class ValidToken {

    @Test
    @DisplayName("should return true when token is authentic, unexpired and belongs to the user")
    void shouldReturnTrueWhenTokenIsValid() {
      User user = UserFixture.buildDefaultUser();

      Mockito.when(jwtProperties.getExpiration()).thenReturn(3600L);

      String token = jwtService.generateJwtToken(user);

      assertThat(jwtService.isValidToken(token, user)).isTrue();
    }

    @Test
    @DisplayName("should return false when token signature is invalid or malformed")
    void shouldReturnFalseWhenTokenIsNotValid() {

      User user = UserFixture.buildDefaultUser();

      String token = "token.fake.token";

      assertThat(jwtService.isValidToken(token, user)).isFalse();
    }
  }
}
