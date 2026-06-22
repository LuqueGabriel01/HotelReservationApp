package com.hotelreservation.catalog.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hotelreservation.catalog.config.properties.HotelCacheProperties;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for setting up and initializing the application's cache
 * infrastructure. Dynamically switches between Caffeine-backed caches and a no-op implementation
 * based on properties.
 */
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {
  public static final String HOTEL_DETAIL_CACHE = "hotelDetail";
  public static final String HOTEL_LIST_CACHE = "hotelList";

  private final HotelCacheProperties properties;

  /**
   * Instantiates the primary {@link CacheManager} bean platform ecosystem. Evaluates active system
   * properties to either bind an operative {@link SimpleCacheManager} instance containing the
   * required functional cache configurations or fallback to a standard {@link NoOpCacheManager}
   * placeholder instance if caching is toggled off.
   *
   * @return The determined active CacheManager instance strategy wrapper.
   */
  @Bean
  public CacheManager cacheManager() {
    if (!properties.enabled()) {
      return new NoOpCacheManager();
    }

    SimpleCacheManager manager = new SimpleCacheManager();
    manager.setCaches(List.of(buildCache(HOTEL_DETAIL_CACHE), buildCache(HOTEL_LIST_CACHE)));
    return manager;
  }

  /**
   * Builds and wraps a standalone Caffeine native container cache mapping context instance
   * initialized with the configured Time-To-Live (TTL) and size limitations parameters.
   *
   * @param name The identification signature string to allocate to the cache segment region.
   * @return A ready-to-use Spring ecosystem abstraction wrapper around the underlying Caffeine data
   *     map.
   */
  private CaffeineCache buildCache(String name) {
    Cache<Object, Object> cache =
        Caffeine.newBuilder()
            .expireAfterWrite(properties.ttlMinutes(), TimeUnit.MINUTES)
            .maximumSize(properties.maxSize())
            .recordStats()
            .build();

    return new CaffeineCache(name, cache);
  }
}
