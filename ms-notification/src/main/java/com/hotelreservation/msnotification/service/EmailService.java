package com.hotelreservation.msnotification.service;

import com.hotelreservation.msnotification.constants.FieldsConstant;
import com.hotelreservation.msnotification.dtos.BookingCancelledEvent;
import com.hotelreservation.msnotification.dtos.BookingConfirmedEvent;
import com.hotelreservation.msnotification.dtos.BookingReminderEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/** Builds and sends the HTML booking notification emails from Thymeleaf templates. */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  private void sendEmail(String to, String subject, String body) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
      helper.setFrom("luquegabrieldoc@gmail.com", "Prueba de app Hoteles");
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(body, true);
      mailSender.send(message);
    } catch (MessagingException | MailException | UnsupportedEncodingException e) {
      log.error(
          "Error sending an email to {} with the subject line '{}': {}",
          to,
          subject,
          e.getMessage());
    }
  }

  /**
   * Sends the booking confirmation email to the guest.
   *
   * @param event the confirmed booking data used to populate the email template
   */
  public void sendBookingConfirmedEmail(BookingConfirmedEvent event) {
    Context context = new Context();
    context.setVariable(FieldsConstant.Booking.USERNAME, event.username());
    context.setVariable(FieldsConstant.Booking.HOTEL_NAME, event.hotelName());
    context.setVariable(FieldsConstant.Booking.ROOM_NAME, event.roomName());
    context.setVariable(FieldsConstant.Booking.CHECK_IN, event.checkIn());
    context.setVariable(FieldsConstant.Booking.CHECK_OUT, event.checkOut());
    context.setVariable(FieldsConstant.Booking.TOTAL_PRICE, event.totalPrice());
    context.setVariable(FieldsConstant.Booking.BOOKING_ID, event.bookingId());

    String htmlBody = templateEngine.process(FieldsConstant.Templates.BOOKING_CONFIRMED, context);
    String subject = "Your reservation at %s is confirmed".formatted(event.hotelName());

    sendEmail(event.userEmail(), subject, htmlBody);
  }

  /**
   * Sends the booking cancellation email to the guest.
   *
   * @param event the cancelled booking data used to populate the email template
   */
  public void sendBookingCancelledEmail(BookingCancelledEvent event) {
    Context context = new Context();
    context.setVariable(FieldsConstant.Booking.USERNAME, event.username());
    context.setVariable(FieldsConstant.Booking.HOTEL_NAME, event.hotelName());
    context.setVariable(FieldsConstant.Booking.CHECK_IN, event.checkIn());
    context.setVariable(FieldsConstant.Booking.ROOM_NAME, event.roomName());
    context.setVariable(FieldsConstant.Booking.CHECK_OUT, event.checkOut());

    String htmlBody = templateEngine.process(FieldsConstant.Templates.BOOKING_CANCELLED, context);
    String subject = "Your reservation at %s has been cancelled".formatted(event.hotelName());

    sendEmail(event.userEmail(), subject, htmlBody);
  }

  /**
   * Sends the check-in reminder email to the guest.
   *
   * @param event the reminder data used to populate the email template
   */
  public void sendBookingReminderEmail(BookingReminderEvent event) {
    Context context = new Context();
    context.setVariable(FieldsConstant.Booking.USERNAME, event.username());
    context.setVariable(FieldsConstant.Booking.HOTEL_NAME, event.hotelName());
    context.setVariable(FieldsConstant.Booking.HOTEL_ADDRESS, event.hotelAddress());
    context.setVariable(FieldsConstant.Booking.ROOM_NAME, event.roomName());
    context.setVariable(FieldsConstant.Booking.CHECK_IN, event.checkIn());
    context.setVariable(FieldsConstant.Booking.CHECK_OUT, event.checkOut());

    String htmlBody = templateEngine.process(FieldsConstant.Templates.BOOKING_REMINDER, context);
    String subject = "Reminder: your check-in at %s is tomorrow".formatted(event.hotelName());

    sendEmail(event.userEmail(), subject, htmlBody);
  }
}
