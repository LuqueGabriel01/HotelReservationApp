package com.hotelreservation.msgateway.config.properties;

import com.hotelreservation.msgateway.constants.ConfigConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Hotel Gateway rate limiter.
 *
 * @param replenishRate The number of tokens added to the bucket per second.
 * @param burstCapacity The maximum number of tokens the bucket can hold.
 * @param requestToken The number of tokens consumed per incoming request.
 */
@ConfigurationProperties(prefix = ConfigConstants.Properties.HOTEL_GATEWAY_RATE_LIMITER)
public record RateLimiterProperties(
    Integer replenishRate, Integer burstCapacity, Integer requestToken) {}
