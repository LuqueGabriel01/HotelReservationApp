# 📧 ms-notification — Notification Service

[← Back to README](../README.md)

**Port:** `8085`
**Stack:** Spring Boot 4 / Java 21 / Maven
**Messaging:** Kafka (consumer + DLQ producer)
**Email:** Brevo SMTP relay via `JavaMailSender`
**Templating:** Thymeleaf (HTML emails)

The `ms-notification` service is responsible for sending email notifications to users. It does not expose public REST endpoints — it operates exclusively by listening to events published on Kafka by `ms-booking`. It reacts to booking confirmations, cancellations, and sends check-in reminders 24 hours before arrival.

---

## Kafka Topics Consumed

| Topic | Trigger | Action |
|---|---|---|
| `booking.confirmed` | A booking has been confirmed | Sends a booking confirmation email |
| `booking.cancelled` | A booking has been cancelled | Sends a booking cancellation email |
| `booking.reminder` | 24 hours before check-in | Sends a check-in reminder email |

> The `booking.reminder` event is scheduled by a background job in `ms-booking` (`@Scheduled(cron = "0 0 8 * * *")`) that scans upcoming check-in dates daily and publishes reminder events for bookings whose check-in is the following day.

### Deserialization

Each topic is consumed through a `JacksonJsonDeserializer` wrapped in Spring Kafka's `ErrorHandlingDeserializer`, with `USE_TYPE_INFO_HEADERS` disabled and an explicit `VALUE_DEFAULT_TYPE` set per consumer factory. This means each factory always deserializes to a single, known DTO, rather than relying on a type header supplied by the producer.

```java
props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, BookingConfirmedEvent.class.getName());
```
---

## Event Payloads

### `booking.confirmed`

```json
{
  "eventType": "booking.confirmed",
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "userEmail": "johndoe@email.com",
  "username": "johndoe",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "hotelName": "Grand Hotel Barcelona",
  "roomName": "Deluxe Double Room",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "totalPrice": 900.00,
  "timestamp": "2025-01-15T10:30:00Z"
}
```

### `booking.cancelled`

```json
{
  "eventType": "booking.cancelled",
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "userEmail": "johndoe@email.com",
  "username": "johndoe",
  "hotelName": "Grand Hotel Barcelona",
  "roomName": "Deluxe Double Room",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "timestamp": "2025-01-16T09:00:00Z"
}
```

### `booking.reminder`

```json
{
  "eventType": "booking.reminder",
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "userEmail": "johndoe@email.com",
  "username": "johndoe",
  "hotelName": "Grand Hotel Barcelona",
  "hotelAddress": "Carrer de Pau Claris, 122, Barcelona",
  "roomName": "Deluxe Double Room",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "timestamp": "2025-03-09T08:00:00Z"
}
```

All three payloads are modeled as immutable Java `record`s, with `UUID` for identifiers, `LocalDate` for check-in/check-out, `Instant` for timestamps, and `BigDecimal` for monetary values.

---

## Email Templates

| Event | Subject | Content |
|---|---|---|
| `booking.confirmed` | `Your reservation at {hotelName} is confirmed` | Booking details, dates, total price, booking ID |
| `booking.cancelled` | `Your reservation at {hotelName} has been cancelled` | Cancelled booking details and dates |
| `booking.reminder` | `Reminder: your check-in at {hotelName} is tomorrow` | Hotel address, room details, check-in time |

Emails are rendered as HTML using Thymeleaf, with inline CSS and table-based layout for compatibility across email clients. Each event type has its own template, resolved by name from `FieldsConstant.Templates` and located in `src/main/resources/templates/`.

Delivery is handled through **Brevo**'s SMTP relay, chosen over a personal SMTP account so the setup mirrors a real transactional email provider — same `JavaMailSender` API, but backed by a service designed for production-style delivery rather than a personal inbox.

---

## Error Handling & Dead Letter Queue

If an email fails to send (SMTP error, authentication failure, etc.), or if a message fails to process for any other reason, Spring Kafka's `DefaultErrorHandler` retries the message with a fixed backoff (1s delay, 2 retries) before giving up.

Once retries are exhausted, the failed message is **not discarded** — it is republished to a dedicated dead-letter topic, `booking.notifications.dlq`, via a `DeadLetterPublishingRecoverer`. This preserves the original message for later inspection or reprocessing, and keeps a single problematic message from blocking the consumer group or being silently lost. The consumer offset is committed regardless, so a poison-pill message can never stall the partition.

```
message fails → retry (1s) → retry (1s) → retries exhausted → republished to booking.notifications.dlq
```

This requires the service to also act as a Kafka producer (see `KafkaProducerConfig`), even though it exposes no other producing behavior.

---

## Configuration

Key environment variables (see `.env`):

| Variable | Purpose |
|---|---|
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address (`localhost:9092` locally, `kafka:9092` in Docker) |
| `EMAIL_HOST` / `EMAIL_PORT` | Brevo SMTP relay host and port |
| `EMAIL_SESSION` | Brevo SMTP login |
| `EMAIL_EXTERNAL_KEY` | Brevo SMTP key |

---

## Local Development

```bash
kafka-console-producer --broker-list localhost:9092 --topic booking.confirmed
```

Paste a single-line JSON payload matching one of the schemas above to trigger an email manually without running `ms-booking`.