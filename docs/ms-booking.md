# 📋 ms-booking — Booking Service

[← Back to README](../README.md)

**Port:** `8084`  
**Database:** PostgreSQL (`booking_db`)  
**Messaging:** Kafka (producer)

The `ms-booking` service manages the full lifecycle of a reservation. It coordinates with `ms-availability` to lock and confirm rooms, and publishes events to Kafka so that other services (such as `ms-notification`) can react automatically.

---

## Data Model

### Entity: `Booking`

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `userId` | UUID | Reference to the user who made the booking |
| `roomId` | UUID | Reference to the booked room |
| `hotelId` | UUID | Reference to the hotel |
| `checkIn` | LocalDate | Check-in date |
| `checkOut` | LocalDate | Check-out date |
| `nights` | Integer | Total number of nights |
| `pricePerNight` | BigDecimal | Price per night at the time of booking |
| `totalPrice` | BigDecimal | Total price (`nights × pricePerNight`) |
| `status` | Enum | Current booking status |
| `createdAt` | Instant | Booking creation timestamp |
| `updatedAt` | Instant | Last update timestamp |

### Enums

```
BookingStatus: PENDING, CONFIRMED, CANCELLED
```

---

## Booking Flow

The booking process involves coordination between multiple services:

1. **Client** calls `POST /api/availability/{roomId}/block` on `ms-availability` to temporarily lock the room.
2. **Client** calls `POST /api/bookings` on `ms-booking` with the `lockId`.
3. **ms-booking** calls `PUT /api/availability/{roomId}/reserve` on `ms-availability` to confirm the lock.
4. **ms-booking** saves the booking with status `CONFIRMED` and publishes a `booking.confirmed` event to Kafka.
5. **ms-notification** consumes the event and sends a confirmation email to the user.

---

## Endpoints

### 1. `POST /api/bookings`

**Access:** Authenticated users

**Description:** Creates and confirms a new booking. Requires a valid `lockId` obtained from `ms-availability`. If the lock has expired, the booking will be rejected.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "lockId": "d4e5f6a7-b8c9-0123-def0-234567890123"
}
```

**Validation Rules:**
- `roomId`, `hotelId`, `checkIn`, `checkOut`, and `lockId` are required.
- `checkOut` must be after `checkIn`.
- The `lockId` must correspond to an active, non-expired block in `ms-availability`.

**Response `201 Created`:**
```json
{
  "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "nights": 5,
  "pricePerNight": 180.00,
  "totalPrice": 900.00,
  "status": "CONFIRMED",
  "createdAt": "2025-01-15T10:30:00Z"
}
```

**Response `409 Conflict`** — when the lock has expired:
```json
{
  "code": 409,
  "name": "CONFLICT",
  "description": "The room lock has expired. Please restart the booking process.",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

**Response `400 Bad Request`:**
```json
{
  "code": 400,
  "name": "BAD_REQUEST",
  "description": "checkOut date must be after checkIn date",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 2. `GET /api/bookings/{id}`

**Access:** Authenticated users (own booking) / ADMIN (any booking)

**Description:** Returns the full detail of a specific booking. A regular user can only retrieve their own bookings.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Booking identifier |

**Response `200 OK`:**
```json
{
  "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "nights": 5,
  "pricePerNight": 180.00,
  "totalPrice": 900.00,
  "status": "CONFIRMED",
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-15T10:30:00Z"
}
```

**Response `403 Forbidden`** — when a user tries to access another user's booking:
```json
{
  "code": 403,
  "name": "FORBIDDEN",
  "description": "You do not have permission to access this booking",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

**Response `404 Not Found`:**
```json
{
  "code": 404,
  "name": "NOT_FOUND",
  "description": "Booking with id e5f6a7b8-c9d0-1234-ef01-345678901234 not found",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 3. `GET /api/bookings/my`

**Access:** Authenticated users

**Description:** Returns all bookings belonging to the currently authenticated user.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `status` | Enum | No | Filter by booking status (`CONFIRMED`, `CANCELLED`) |
| `page` | Integer | No | Page number (default: 0) |
| `size` | Integer | No | Page size (default: 10) |

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
      "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "checkIn": "2025-03-10",
      "checkOut": "2025-03-15",
      "totalPrice": 900.00,
      "status": "CONFIRMED"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

---

### 4. `DELETE /api/bookings/{id}`

**Access:** Authenticated users (own booking) / ADMIN (any booking)

**Description:** Cancels an existing booking. The service calls `ms-availability` to release the room and publishes a `booking.cancelled` event to Kafka. Only bookings with status `CONFIRMED` can be cancelled.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Booking identifier |

**Response `200 OK`:**
```json
{
  "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "status": "CANCELLED",
  "updatedAt": "2025-01-16T09:00:00Z"
}
```

**Response `409 Conflict`** — when the booking is already cancelled:
```json
{
  "code": 409,
  "name": "CONFLICT",
  "description": "Booking is already cancelled",
  "timestamp": "2025-01-16T09:00:00Z"
}
```

---

### 5. `GET /api/bookings`

**Access:** ADMIN only

**Description:** Returns a paginated list of all bookings in the system.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `status` | Enum | No | Filter by booking status |
| `page` | Integer | No | Page number (default: 0) |
| `size` | Integer | No | Page size (default: 20) |

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "checkIn": "2025-03-10",
      "checkOut": "2025-03-15",
      "totalPrice": 900.00,
      "status": "CONFIRMED"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

---

### 6. `GET /api/bookings/hotel/{hotelId}`

**Access:** ADMIN only

**Description:** Returns all bookings for a specific hotel.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `hotelId` | UUID | Hotel identifier |

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `status` | Enum | No | Filter by booking status |
| `page` | Integer | No | Page number (default: 0) |
| `size` | Integer | No | Page size (default: 20) |

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "checkIn": "2025-03-10",
      "checkOut": "2025-03-15",
      "totalPrice": 900.00,
      "status": "CONFIRMED"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

---

## Kafka Events Published

| Topic | Trigger | Payload |
|---|---|---|
| `booking.confirmed` | Booking is created and confirmed | `bookingId`, `userId`, `hotelId`, `roomId`, `checkIn`, `checkOut`, `totalPrice` |
| `booking.cancelled` | Booking is cancelled | `bookingId`, `userId`, `hotelId`, `checkIn`, `checkOut` |

### Example Payload — `booking.confirmed`

```json
{
  "eventType": "booking.confirmed",
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "totalPrice": 900.00,
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

## Error Format

All error responses follow a consistent structure:

| Field | Type | Description |
|---|---|---|
| `code` | Integer | HTTP status code |
| `name` | String | Short error identifier |
| `description` | String | Human-readable error message |
| `timestamp` | Instant | Time at which the error occurred |