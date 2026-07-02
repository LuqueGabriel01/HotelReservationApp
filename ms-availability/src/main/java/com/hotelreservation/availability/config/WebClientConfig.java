package com.hotelreservation.availability.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/** Configuration class for WebClient beans. */
@Configuration
public class WebClientConfig {

  /**
   * Creates a configured WebClient bean for the Catalog service.
   *
   * @param catalogUrl The base URL configured for the catalog service.
   * @return The configured WebClient instance.
   */
  @Bean
  public WebClient catalogWebClient(@Value("${services.catalog.url}") String catalogUrl) {
    return WebClient.builder().baseUrl(catalogUrl).build();
  }
}
