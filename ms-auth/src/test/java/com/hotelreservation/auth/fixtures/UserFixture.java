package com.hotelreservation.auth.fixtures;

import com.hotelreservation.auth.enums.Role;
import com.hotelreservation.auth.models.entities.User;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class providing pre-configured {@link User} entity fixtures for testing purposes.
 *
 * <p>This class implements the Object Mother pattern to streamline test data generation, offering
 * reusable factory methods to construct user entities with standard default or alternative
 * credentials and profiles.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserFixture {

  public static final String DEFAULT_NAME = "jhon";
  public static final String DEFAULT_EMAIL = "jhon@email.com";
  public static final String DEFAULT_PASSWORD = "doe";

  public static final String OTHER_USERNAME = "other_username";
  public static final String OTHER_EMAIL = "other@email.com";

  /**
   * Builds a standard {@link User} entity pre-populated with the default name, email, password, and
   * assigned the {@link Role#ROLE_USER} role.
   *
   * @return a pre-configured default {@link User} instance
   */
  public static User buildDefaultUser() {
    return User.builder()
        .id(UUID.randomUUID())
        .username(DEFAULT_NAME)
        .email(DEFAULT_EMAIL)
        .password(DEFAULT_PASSWORD)
        .role(Role.ROLE_USER)
        .build();
  }

  /**
   * Builds an alternative {@link User} entity pre-populated with secondary credentials (other
   * username and email), the default password, and assigned the {@link Role#ROLE_USER} role.
   *
   * @return a pre-configured secondary {@link User} instance to prevent unique constraint conflicts
   */
  public static User buildOtherUser() {
    return User.builder()
        .id(UUID.randomUUID())
        .username(OTHER_USERNAME)
        .email(OTHER_EMAIL)
        .password(DEFAULT_PASSWORD)
        .role(Role.ROLE_USER)
        .build();
  }
}
