package com.hotelreservation.msnotification.consumer;

import com.hotelreservation.msnotification.constants.FieldsConstant;
import com.hotelreservation.msnotification.dtos.BookingConfirmedEvent;
import com.hotelreservation.msnotification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Listens for {@code booking.confirmed} events and triggers the confirmation email. */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingConfirmedConsumer {

  private final EmailService emailService;

  /**
   * Consumes a {@code booking.confirmed} message and delegates the sending of the confirmation
   * email.
   *
   * @param event the confirmation details published by ms-booking
   */
  @KafkaListener(
      topics = FieldsConstant.Topics.BOOKING_CONFIRMED,
      containerFactory = "bookingConfirmedListenerContainerFactory")
  public void consume(BookingConfirmedEvent event) {
    log.info("Received booking.confirmed event for bookingId={}", event.bookingId());
    emailService.sendBookingConfirmedEmail(event);
  }
}
