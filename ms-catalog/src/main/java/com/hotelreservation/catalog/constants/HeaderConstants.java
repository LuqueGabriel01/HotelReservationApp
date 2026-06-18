package com.hotelreservation.catalog.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/** Centralized definition of HTTP header names and security-related constants. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderConstants {

    /** HTTP headers and tokens used for security, authentication, and routing context. */
    public static final class Security {
        public static final String ROLE_CLAIMS = "role";
        public static final String BEARER_PREFIX = "Bearer ";
        public static final String AUTHORIZATION = "Authorization";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String X_USER_ID = "X-User-Id";
        public static final String X_USER_ROLE = "X-User-Role";
    }

    /** Predefined configurations and allowed headers for CORS policy. */
    public static final class Cors {
        public static final List<String> ALLOWED_HEADERS =
                List.of(
                        Security.AUTHORIZATION,
                        Security.CONTENT_TYPE,
                        Security.X_USER_ID,
                        Security.X_USER_ROLE);
    }
}
