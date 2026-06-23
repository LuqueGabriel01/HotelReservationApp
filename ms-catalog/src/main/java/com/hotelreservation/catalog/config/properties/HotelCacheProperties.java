package com.hotelreservation.catalog.config.properties;

import com.hotelreservation.catalog.constants.ConfigConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration properties for the hotel catalog cache system.
 *
 * @param enabled flag to enable or disable the cache mechanism
 * @param ttlMinutes time-to-live duration for cache entries in minutes
 * @param maxSize maximum number of entries allowed in the cache memory
 */
@ConfigurationProperties(prefix = ConfigConstants.Properties.HOTEL_CATALOG_CACHE)
public record HotelCacheProperties(
    @DefaultValue("true") boolean enabled,
    @DefaultValue("10") long ttlMinutes,
    @DefaultValue("500") long maxSize) {}
