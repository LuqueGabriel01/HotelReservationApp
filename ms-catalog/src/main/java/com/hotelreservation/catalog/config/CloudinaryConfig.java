package com.hotelreservation.catalog.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hotelreservation.catalog.config.properties.CloudinaryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for setting up and initializing the Cloudinary client environment
 * SDK integration bean ecosystem.
 */
@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

  private final CloudinaryProperties properties;

  /**
   * Instantiates and configures the primary {@link Cloudinary} client bean context. Maps the
   * credential values (cloud name, API key, and API secret) provided by the external application
   * properties configuration pipeline to construct a thread-safe network bridge.
   *
   * @return The configured Cloudinary client API gateway engine instance wrapper.
   */
  @Bean
  public Cloudinary cloudinary() {
    return new Cloudinary(
        ObjectUtils.asMap(
            "cloud_name",
            properties.cloudName(),
            "api_key",
            properties.apiKey(),
            "api_secret",
            properties.apiSecret()));
  }
}
