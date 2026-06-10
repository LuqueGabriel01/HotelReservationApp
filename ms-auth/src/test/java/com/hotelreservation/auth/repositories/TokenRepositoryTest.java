package com.hotelreservation.auth.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotelreservation.auth.config.PostgresTestContainer;
import com.hotelreservation.auth.fixtures.TokenFixture;
import com.hotelreservation.auth.fixtures.UserFixture;
import com.hotelreservation.auth.models.entities.Token;
import com.hotelreservation.auth.models.entities.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests for {@link TokenRepository} using a real PostgreSQL instance provided by
 * Testcontainers.
 */
@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
class TokenRepositoryTest {

  @Autowired private TokenRepository tokenRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private TestEntityManager entityManager;

  private User user;

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = PostgresTestContainer.INSTANCE;

  @BeforeEach
  void setUp() {
    tokenRepository.deleteAll();
    userRepository.deleteAll();
    user = userRepository.save(UserFixture.buildDefaultUser());
  }

  @Test
  @DisplayName("findByToken() → returns a token with the user loaded (JOIN FETCH)")
  void findByToken_shouldReturnTokenWithUserLoaded() {
    tokenRepository.save(TokenFixture.buildActiveAccessToken(user));

    Optional<Token> result = tokenRepository.findByToken(TokenFixture.DEFAULT_ACCESS_TOKEN);

    assertThat(result).isPresent();
    assertThat(result.get().getUser()).isNotNull();
    assertThat(result.get().getUser().getEmail()).isEqualTo(UserFixture.DEFAULT_EMAIL);
  }

  @Test
  @DisplayName("findByToken() → returns empty if the token does not exist")
  void findByToken_shouldReturnEmptyWhenNotFound() {
    assertThat(tokenRepository.findByToken("token.inexistente")).isEmpty();
  }

  @Test
  @DisplayName("revokeAllActiveByUserId() → revokes all active tokens")
  void revokeAllActiveByUserId_shouldRevokeAllActiveTokens() {
    tokenRepository.saveAll(
        List.of(TokenFixture.buildActiveAccessToken(user), TokenFixture.buildActiveRefresh(user)));

    tokenRepository.revokeAllActiveByUserId(user.getId(), Instant.now());

    entityManager.clear();

    assertThat(tokenRepository.findAll())
        .hasSize(2)
        .allSatisfy(
            t -> {
              assertThat(t.isRevoked()).isTrue();
              assertThat(t.isExpired()).isTrue();
              assertThat(t.getClosedAt()).isNotNull();
            });
  }

  @Test
  @DisplayName("revokeAllActiveByUserId() → no toca tokens ya inactivos")
  void revokeAllActiveByUserId_shouldIgnoreAlreadyInactiveTokens() {
    Token inactiveToken = tokenRepository.save(TokenFixture.buildInactive(user));

    tokenRepository.revokeAllActiveByUserId(user.getId(), Instant.now());

    Token result = tokenRepository.findById(inactiveToken.getId()).orElseThrow();
    assertThat(result.getClosedAt()).isNull();
  }

  @Test
  @DisplayName("revokeAllActiveByUserId() → no afecta tokens de otros usuarios")
  void revokeAllActiveByUserId_shouldNotAffectOtherUsers() {
    User otherUser = userRepository.save(UserFixture.buildOtherUser());

    tokenRepository.save(TokenFixture.buildActiveAccessToken(user));
    Token otherToken =
        tokenRepository.save(
            TokenFixture.buildActiveAccessWithValue(otherUser, "other.user.token"));

    tokenRepository.revokeAllActiveByUserId(user.getId(), Instant.now());

    Token untouched = tokenRepository.findById(otherToken.getId()).orElseThrow();
    assertThat(untouched.isRevoked()).isFalse();
    assertThat(untouched.isExpired()).isFalse();
  }
}
