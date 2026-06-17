package com.hotelreservation.msgateway.config.properties;

import com.hotelreservation.msgateway.constants.ConfigConstants;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Cross-Origin Resource Sharing (CORS) filtering in the Hotel Gateway.
 *
 * <p>These properties are automatically linked from configuration files (e.g. {@code
 * application.yml}) using the prefix defined in {@link
 * ConfigConstants.Properties#HOTEL_GATEWAY_CORS}.
 *
 * @param allowedOrigins List of allowed origins (URLs) authorized to make requests to the gateway.
 * @param allowedMethods List of allowed HTTP methods (e.g. {@code GET}, {@code POST}, {@code PUT},
 *     {@code DELETE}) for CORS requests.
 * @param maxAge Maximum time (in seconds) that the client’s browser may cache the result of a
 *     preflight request.
 */
@ConfigurationProperties(prefix = ConfigConstants.Properties.HOTEL_GATEWAY_CORS)
public record CorsProperties(
    List<String> allowedOrigins, List<String> allowedMethods, Long maxAge) {}
