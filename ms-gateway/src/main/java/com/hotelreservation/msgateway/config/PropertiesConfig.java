package com.hotelreservation.msgateway.config;

import com.hotelreservation.msgateway.constants.ConfigConstants;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration responsible for enabling the scanning of classes annotated with.
 * {@code @ConfigurationProperties}
 */
@Configuration
@ConfigurationPropertiesScan(basePackages = ConfigConstants.BASE_PACKAGE)
public class PropertiesConfig {}
