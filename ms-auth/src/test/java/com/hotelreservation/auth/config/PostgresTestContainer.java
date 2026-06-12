package com.hotelreservation.auth.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Utility class that provides a single, shared instance of a PostgreSQL TestContainer for
 * integration testing.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostgresTestContainer {

  private static final String POSTGRES_IMAGE = "postgres:18.3-alpine";

  public static final PostgreSQLContainer<?> INSTANCE = new PostgreSQLContainer<>(POSTGRES_IMAGE);
}
