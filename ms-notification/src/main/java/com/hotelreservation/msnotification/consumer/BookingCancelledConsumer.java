package com.hotelreservation.msnotification.consumer;

import com.hotelreservation.msnotification.constants.FieldsConstant;
import com.hotelreservation.msnotification.dtos.BookingCancelledEvent;
import com.hotelreservation.msnotification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Listens for {@code booking.cancelled} events and triggers the cancellation email. */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCancelledConsumer {

  private final EmailService emailService;

  /**
   * Consumes a {@code booking.cancelled} message and delegates the sending of the cancellation
   * email.
   *
   * @param event the cancellation details published by ms-booking
   */
  @KafkaListener(
      topics = FieldsConstant.Topics.BOOKING_CANCELLED,
      containerFactory = "bookingCancelledListenerContainerFactory")
  public void consume(BookingCancelledEvent event) {
    log.info("Received booking.cancelled event for bookingId={}", event.bookingId());
    emailService.sendBookingCancelledEmail(event);
  }
}
