package com.hotelreservation.catalog.config.properties;

import com.hotelreservation.catalog.constants.ConfigConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = ConfigConstants.Properties.CLOUDINARY)
public record CloudinaryProperties(
        String cloudName,
        String apiKey,
        String apiSecret
) {
}
