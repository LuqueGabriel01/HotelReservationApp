package com.hotelreservation.catalog.config.properties;

import com.hotelreservation.catalog.constants.ConfigConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Cloudinary integration.
 *
 * @param cloudName the Cloudinary cloud name account identifier
 * @param apiKey the API key for authentication
 * @param apiSecret the API secret for authentication
 */
@ConfigurationProperties(prefix = ConfigConstants.Properties.CLOUDINARY)
public record CloudinaryProperties(String cloudName, String apiKey, String apiSecret) {}
