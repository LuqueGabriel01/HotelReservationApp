package com.hotelreservation.msagent.config.properties;

import static com.hotelreservation.msagent.constants.ConfigConstants.Properties.CATALOG;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Connection settings for the ms-catalog service. */
@ConfigurationProperties(prefix = CATALOG)
public record CatalogProperties(String baseUrl) {}
