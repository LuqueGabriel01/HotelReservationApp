package com.hotelreservation.catalog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cache.hotel")
@Getter
@Setter
public class HotelCacheProperties {
    private boolean enabled = true;
    private long ttlMinutes = 10;
    private long maxSize = 500;

}
