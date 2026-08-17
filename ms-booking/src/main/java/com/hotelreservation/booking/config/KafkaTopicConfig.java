package com.hotelreservation.booking.config;

import com.hotelreservation.booking.constants.ConfigConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Configuration class for creating Kafka topics. */
@Configuration
public class KafkaTopicConfig {

  /**
   * Creates the topic for booking cancellation events.
   *
   * @return configured NewTopic instance for booking cancellations
   */
  @Bean
  public NewTopic bookingCancelledTopic() {
    return TopicBuilder.name(ConfigConstants.KafkaTopics.BOOKING_CANCELLED)
        .partitions(1)
        .replicas(1)
        .build();
  }

  /**
   * Creates the topic for booking confirmation events.
   *
   * @return configured NewTopic instance for booking confirmations
   */
  @Bean
  public NewTopic bookingConfirmedTopic() {
    return TopicBuilder.name(ConfigConstants.KafkaTopics.BOOKING_CONFIRMED)
        .partitions(1)
        .replicas(1)
        .build();
  }

  /**
   * Creates the topic for booking reminder events.
   *
   * @return configured NewTopic instance for booking reminders
   */
  @Bean
  public NewTopic bookingReminderTopic() {
    return TopicBuilder.name(ConfigConstants.KafkaTopics.BOOKING_REMINDER)
        .partitions(1)
        .replicas(1)
        .build();
  }
}
