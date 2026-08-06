package com.hotelreservation.booking.config;

import com.hotelreservation.booking.config.properties.ServicesProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/** Configuration class for initializing WebClient instances. */
@Configuration
public class ClientConfig {

  /**
   * Creates a WebClient bean configured for the catalog service.
   *
   * @param props configuration properties containing service URLs
   * @return configured WebClient instance for the catalog service
   */
  @Bean
  public WebClient catalogWebClient(ServicesProperties props) {
    return WebClient.builder().baseUrl(props.catalogUrl()).build();
  }

  /**
   * Creates a WebClient bean configured for the availability service.
   *
   * @param props configuration properties containing service URLs
   * @return configured WebClient instance for the availability service
   */
  @Bean
  public WebClient availabilityWebClient(ServicesProperties props) {
    return WebClient.builder().baseUrl(props.availabilityUrl()).build();
  }

  /**
   * Creates a WebClient bean configured for the authentication service.
   *
   * @param props configuration properties containing service URLs
   * @return configured WebClient instance for the authentication service
   */
  @Bean
  public WebClient authWebClient(ServicesProperties props) {
    return WebClient.builder().baseUrl(props.authUrl()).build();
  }
}
