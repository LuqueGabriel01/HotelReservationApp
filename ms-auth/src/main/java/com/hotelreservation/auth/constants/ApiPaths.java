package com.hotelreservation.auth.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized definition of API endpoint paths. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiPaths {

  /** Authentication endpoints. */
  public static final class Auth {
    public static final String BASE_URL = "/api/auth";
    public static final String LOGIN = "/api/auth/login";
    public static final String REGISTER = "/api/auth/register";
    public static final String REFRESH = "/api/auth/refresh";
    public static final String LOGOUT = "/api/auth/logout";
    public static final String ME = "/api/auth/me";
    public static final String INTERNAL_ME = "/api/auth/internal/{userId}";
  }

  /** API documentation endpoints. */
  public static final class Docs {
    public static final String SWAGGER_UI = "/swagger-ui/**";
    public static final String API_DOCS = "/v3/api-docs/**";
  }

  /** Actuator endpoints for monitoring and health checks. */
  public static final class Actuator {
    public static final String HEALTH = "/actuator/health";
  }
}
