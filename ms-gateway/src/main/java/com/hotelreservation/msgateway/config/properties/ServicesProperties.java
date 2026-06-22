package com.hotelreservation.msgateway.config.properties;

import com.hotelreservation.msgateway.constants.ConfigConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for external service URLs or identifiers within the Hotel Gateway.
 *
 * @param auth The endpoint for authentication service.
 * @param catalog The endpoint for hotel catalog service.
 * @param availability The endpoint for room availability service.
 * @param agent The endpoint for agent service.
 * @param booking The endpoint for reservation and booking service.
 */
@ConfigurationProperties(prefix = ConfigConstants.Properties.HOTEL_GATEWAY_SERVICE)
public record ServicesProperties(
    String auth, String catalog, String availability, String agent, String booking) {}
