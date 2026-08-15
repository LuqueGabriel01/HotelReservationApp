package com.hotelreservation.msagent.config;

import static com.hotelreservation.msagent.constants.ConfigConstants.BASE_PACKAGE;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration responsible for enabling the scanning of classes annotated with.
 * {@code @ConfigurationProperties}
 */
@Configuration
@ConfigurationPropertiesScan(basePackages = BASE_PACKAGE)
public class PropertiesConfig {}
