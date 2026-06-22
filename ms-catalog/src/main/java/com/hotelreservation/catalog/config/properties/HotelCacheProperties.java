package com.hotelreservation.catalog.config.properties;

import com.hotelreservation.catalog.constants.ConfigConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = ConfigConstants.Properties.HOTEL_CATALOG_CACHE)
public record HotelCacheProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("10") long ttlMinutes,
        @DefaultValue("500") long maxSize
) { }
