package com.hotelreservation.booking.config.properties;

import static com.hotelreservation.booking.constants.ConfigConstants.Properties.HOTEL_SERVICES;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for external hotel service URLs.
 *
 * @param catalogUrl base URL for the catalog service
 * @param availabilityUrl base URL for the availability service
 * @param authUrl base URL for the authentication service
 */
@ConfigurationProperties(prefix = HOTEL_SERVICES)
public record ServicesProperties(String catalogUrl, String availabilityUrl, String authUrl) {}
