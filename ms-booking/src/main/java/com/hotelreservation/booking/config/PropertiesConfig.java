package com.hotelreservation.booking.config;

import com.hotelreservation.booking.constants.ConfigConstants;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration responsible for enabling the scanning of classes annotated with.
 * {@code @ConfigurationProperties}
 */
@Configuration
@ConfigurationPropertiesScan(basePackages = ConfigConstants.BASE_PACKAGE)
public class PropertiesConfig {}
