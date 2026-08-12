package com.hotelreservation.msnotification.config;

import static com.hotelreservation.msnotification.constants.ConfigConstants.BASE_PACKAGE_DTO;

import com.hotelreservation.msnotification.dtos.BookingCancelledEvent;
import com.hotelreservation.msnotification.dtos.BookingConfirmedEvent;
import com.hotelreservation.msnotification.dtos.BookingReminderEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

/** Kafka consumer configuration for the booking event listeners. */
@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {

  private final KafkaProperties kafkaProperties;

  private Map<String, Object> consumerProps() {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
    props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, BASE_PACKAGE_DTO);
    props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
    return props;
  }

  /**
   * Builds the Kafka consumer factory used to deserialize {@link BookingConfirmedEvent} messages.
   *
   * @return the configured consumer factory for the {@code booking.confirmed} topic
   */
  @Bean
  public ConsumerFactory<String, BookingConfirmedEvent> bookingConfirmedConsumerFactory() {
    Map<String, Object> props = consumerProps();
    props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, BookingConfirmedEvent.class.getName());
    return new DefaultKafkaConsumerFactory<>(props);
  }

  /**
   * Builds the listener container factory used by the {@code booking.confirmed} Kafka listener.
   *
   * @return the configured listener container factory for {@link BookingConfirmedEvent} messages
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, BookingConfirmedEvent>
      bookingConfirmedListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, BookingConfirmedEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(bookingConfirmedConsumerFactory());
    return factory;
  }

  /**
   * Builds the Kafka consumer factory used to deserialize {@link BookingCancelledEvent} messages.
   *
   * @return the configured consumer factory for the {@code booking.cancelled} topic
   */
  @Bean
  public ConsumerFactory<String, BookingCancelledEvent> bookingCancelledConsumerFactory() {
    Map<String, Object> props = consumerProps();
    props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, BookingCancelledEvent.class.getName());
    return new DefaultKafkaConsumerFactory<>(props);
  }

  /**
   * Builds the listener container factory used by the {@code booking.cancelled} Kafka listener.
   *
   * @return the configured listener container factory for {@link BookingCancelledEvent} messages
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, BookingCancelledEvent>
      bookingCancelledListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, BookingCancelledEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(bookingCancelledConsumerFactory());
    return factory;
  }

  /**
   * Builds the Kafka consumer factory used to deserialize {@link BookingReminderEvent} messages.
   *
   * @return the configured consumer factory for the {@code booking.reminder} topic
   */
  @Bean
  public ConsumerFactory<String, BookingReminderEvent> bookingReminderConsumerFactory() {
    Map<String, Object> props = consumerProps();
    props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, BookingReminderEvent.class.getName());
    return new DefaultKafkaConsumerFactory<>(props);
  }

  /**
   * Builds the listener container factory used by the {@code booking.reminder} Kafka listener.
   *
   * @return the configured listener container factory for {@link BookingReminderEvent} messages
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, BookingReminderEvent>
      bookingReminderListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, BookingReminderEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(bookingReminderConsumerFactory());
    return factory;
  }
}
