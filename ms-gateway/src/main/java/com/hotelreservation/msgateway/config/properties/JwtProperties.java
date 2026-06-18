package com.hotelreservation.msgateway.config.properties;

import com.hotelreservation.msgateway.constants.ConfigConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Hotel Gateway JWT authentication.
 *
 * @param secret The secret key used for signing and verifying JWT tokens.
 * @param tokenPrefix The prefix string (e.g., Bearer ) applied to the JWT token header.
 */
@ConfigurationProperties(prefix = ConfigConstants.Properties.HOTEL_GATEWAY_JWT)
public record JwtProperties(String secret, String tokenPrefix) {}
