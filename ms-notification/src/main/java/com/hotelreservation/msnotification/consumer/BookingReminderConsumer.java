package com.hotelreservation.msnotification.consumer;

import com.hotelreservation.msnotification.constants.FieldsConstant;
import com.hotelreservation.msnotification.dtos.BookingReminderEvent;
import com.hotelreservation.msnotification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Listens for {@code booking.reminder} events and triggers the check-in reminder email. */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingReminderConsumer {

  private final EmailService emailService;

  /**
   * Consumes a {@code booking.reminder} message and delegates the sending of the check-in reminder
   * email.
   *
   * @param event the reminder details published by ms-booking
   */
  @KafkaListener(
      topics = FieldsConstant.Topics.BOOKING_REMINDER,
      containerFactory = "bookingReminderListenerContainerFactory")
  public void consume(BookingReminderEvent event) {
    log.info("Received booking.reminder event for bookingId={}", event.bookingId());
    emailService.sendBookingReminderEmail(event);
  }
}
