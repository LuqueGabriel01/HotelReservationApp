package com.hotelreservation.msagent.config.properties;

import static com.hotelreservation.msagent.constants.ConfigConstants.Properties.CHAT;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Settings for chat session storage. */
@ConfigurationProperties(prefix = CHAT)
public record ChatProperties(Integer sessionTtlMinutes) {}
