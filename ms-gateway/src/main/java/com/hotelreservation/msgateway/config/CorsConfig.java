package com.hotelreservation.msgateway.config;

import com.hotelreservation.msgateway.config.properties.CorsProperties;
import com.hotelreservation.msgateway.constants.ApiPaths;
import com.hotelreservation.msgateway.constants.HeaderConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Configuration class to set up Cross-Origin Resource Sharing (CORS) for the gateway application.
 * This ensures secure cross-origin requests based on custom properties and predefined headers.
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

  private final CorsProperties corsProperties;

  /**
   * Creates and configures a reactive {@link CorsWebFilter} applied to all gateway routes.
   *
   * @return a configured {@link CorsWebFilter} instance with allowed origins, methods, headers,
   *     credentials, and max age settings.
   */
  @Bean
  public CorsWebFilter corsFilter() {

    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(corsProperties.allowedOrigins());
    config.setAllowedMethods(corsProperties.allowedMethods());
    config.setAllowedHeaders(HeaderConstants.Cors.ALLOWED_HEADERS);
    config.setMaxAge(corsProperties.maxAge());
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(ApiPaths.GatewaysRoutes.ALL, config);

    return new CorsWebFilter(source);
  }
}
