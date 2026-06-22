package com.hotelreservation.msgateway.constants;

import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

/** Centralized definition of API endpoint paths and routing patterns. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiPaths {

  /** Routing path patterns for matching and forwarding traffic to microservices. */
  public static final class GatewaysRoutes {
    public static final String ALL = "/**";
    public static final String FORWARD_FALLBACK = "forward:/fallback";
    public static final String AUTH = "/api/auth/**";
    public static final String AI = "/api/ai/**";
    public static final String AVAILABILITY = "/api/availability/**";
    public static final String BOOKING = "/api/bookings/**";
    public static final String CATALOG = "/api/hotels/**";
    public static final String FALLBACK = "/fallback";
  }

  /** Definitions of endpoints that are publicly accessible. */
  public static final class PublicRoutes {

    /** Public endpoints accessible via HTTP POST requests. */
    public static final class Post {
      public static final String REGISTER = "/api/auth/register";
      public static final String LOGIN = "/api/auth/login";
      public static final String REFRESH = "/api/auth/refresh";
    }

    /** Public endpoints accessible via HTTP GET requests. */
    public static final class Get {
      public static final String HOTELS = "/api/hotels";
      public static final String HOTELS_ID = "/api/hotels/{id}";
      public static final String HOTELS_ID_ROOM = "/api/hotels/{id}/rooms";
      public static final String HOTELS_ID_ROOM_ID = "/api/hotels/{id}/rooms/{roomId}";
    }

    /**
     * Registry mapping HTTP methods to their respective sets of publicly allowed endpoint paths.
     */
    public static final Map<HttpMethod, Set<String>> ALLOWED_PATHS =
        Map.of(
            HttpMethod.POST, Set.of(Post.REFRESH, Post.REGISTER, Post.LOGIN),
            HttpMethod.GET,
                Set.of(Get.HOTELS, Get.HOTELS_ID, Get.HOTELS_ID_ROOM, Get.HOTELS_ID_ROOM_ID));
  }
}
