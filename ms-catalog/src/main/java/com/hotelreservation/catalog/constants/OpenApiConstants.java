package com.hotelreservation.catalog.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized codes and description messages for OpenAPI. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenApiConstants {

    public static final String BEARER_AUTH = "BearerAuth";

    /**
     * Centralized codes for OpenAPI.
     */
    public static final class Code {
        public static final String SUCCESS = "200";
        public static final String CREATED = "202";
        public static final String NO_CONTENT = "204";
        public static final String BAD_REQUEST = "400";
        public static final String UNAUTHORIZED = "401";
        public static final String FORBIDDEN = "403";
        public static final String NOT_FOUND = "404";
        public static final String CONFLICT = "409";
        public static final String INTERNAL_SERVER_ERROR = "500";

    }
}