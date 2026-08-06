package com.hotelreservation.booking.services.jobs;

import com.hotelreservation.booking.client.AuthClient;
import com.hotelreservation.booking.constants.ConfigConstants;
import com.hotelreservation.booking.exceptions.NotFoundException;
import com.hotelreservation.booking.mappers.BookingMapper;
import com.hotelreservation.booking.models.dtos.event.BookingReminderPayload;
import com.hotelreservation.booking.models.dtos.internal.InternalAuthInfo;
import com.hotelreservation.booking.models.entities.Booking;
import com.hotelreservation.booking.models.enums.BookingStatus;
import com.hotelreservation.booking.repositories.BookingRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/** Scheduled service responsible for sending upcoming check-in reminders to users via Kafka. */
@Service
@Slf4j
@RequiredArgsConstructor
public class BookingReminderService {

  private final BookingRepository bookingRepository;
  private final AuthClient authClient;
  private final BookingMapper mapper;
  private final KafkaTemplate<String, Object> kafka;

  /**
   * Scheduled daily task to process and publish check-in reminders for bookings starting tomorrow.
   */
  @Scheduled(cron = "0 0 8 * * *")
  public void sendCheckInReminders() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    List<Booking> bookings =
        bookingRepository.findByCheckInIsAndStatus(tomorrow, BookingStatus.CONFIRMED);

    bookings.forEach(
        booking -> {
          try {
            InternalAuthInfo auth = authClient.getAuthInfo(booking.getUserId());
            BookingReminderPayload payload = mapper.toRemindedPayload(booking, auth);
            kafka.send(ConfigConstants.KafkaTopics.BOOKING_REMINDER, payload);
          } catch (NotFoundException | WebClientRequestException e) {
            log.error("Failed to send reminder for booking {}", booking.getId(), e);
          }
        });
  }
}
