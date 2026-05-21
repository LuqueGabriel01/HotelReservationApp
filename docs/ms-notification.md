# 📧 ms-notification — Notification Service

[← Back to README](../README.md)

**Port:** `8085`  
**Messaging:** Kafka (consumer)

The `ms-notification` service is responsible for sending email notifications to users. It does not expose public REST endpoints — it operates exclusively by listening to events published on Kafka by `ms-booking`. It reacts to booking confirmations, cancellations, and sends check-in reminders 24 hours before arrival.

---

## Kafka Topics Consumed

| Topic | Trigger | Action |
|---|---|---|
| `booking.confirmed` | A booking has been confirmed | Sends a booking confirmation email |
| `booking.cancelled` | A booking has been cancelled | Sends a booking cancellation email |
| `booking.reminder` | 24 hours before check-in | Sends a check-in reminder email |

> The `booking.reminder` event is scheduled by a background job that scans upcoming check-in dates daily and publishes reminder events for bookings whose check-in is the following day.

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

---

## Email Templates

| Event | Subject | Content |
|---|---|---|
| `booking.confirmed` | `Your reservation at {hotelName} is confirmed` | Booking details, dates, total price, booking ID |
| `booking.cancelled` | `Your reservation at {hotelName} has been cancelled` | Cancelled booking details and dates |
| `booking.reminder` | `Reminder: your check-in at {hotelName} is tomorrow` | Hotel address, room details, check-in time |

---

## Error Handling

If an email fails to send, the service logs the error and does **not** re-publish the event. Kafka's consumer group offset is committed regardless to avoid infinite retry loops. For production environments, a dead-letter topic (`booking.notifications.dlq`) is recommended for failed deliveries.