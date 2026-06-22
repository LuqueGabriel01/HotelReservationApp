# 📋 ms-booking — Booking Service

[← Back to README](../README.md)

**Port:** `8083`  
**Database:** PostgreSQL (`booking_db`)  
**Messaging:** Kafka (producer)

The `ms-booking` service manages the full lifecycle of a reservation. It coordinates with `ms-availability` to lock and confirm rooms, fetches room and hotel data from `ms-catalog` at creation time, and publishes events to Kafka so that `ms-notification` reacts automatically.

---

## Database — `booking_db`

### Table: `bookings`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | UUID | PK | Unique identifier |
| `user_id` | UUID | NOT NULL | User who made the booking (external ref — ms-auth) |
| `hotel_id` | UUID | NOT NULL | Hotel reference (external ref — ms-catalog) |
| `hotel_name` | VARCHAR | NOT NULL | Hotel name snapshot at booking time |
| `room_id` | UUID | NOT NULL | Room reference (external ref — ms-catalog) |
| `room_type` | VARCHAR | NOT NULL | Room type snapshot at booking time |
| `check_in` | DATE | NOT NULL | Check-in date |
| `check_out` | DATE | NOT NULL | Check-out date |
| `nights` | INT | NOT NULL | Number of nights (computed) |
| `price_per_night` | DECIMAL | NOT NULL | Price snapshot at booking time |
| `total_price` | DECIMAL | NOT NULL | Total price (`nights × price_per_night`) |
| `status` | ENUM | NOT NULL | Current booking status |
| `created_at` | TIMESTAMP | NOT NULL | Booking creation timestamp |

### Table: `booking_history`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | UUID | PK | Unique identifier |
| `booking_id` | UUID | FK → bookings, NOT NULL | Booking this record belongs to |
| `previous_status` | ENUM | NOT NULL | Status before the change |
| `new_status` | ENUM | NOT NULL | Status after the change |
| `changed_at` | TIMESTAMP | NOT NULL | When the change occurred |
| `reason` | VARCHAR | nullable | Reason for the change (e.g. "Cancelled by user") |

### Enums

```
BookingStatus: PENDIENTE, CONFIRMADA, CANCELADA
```

### Relationships

- `bookings` 1 → N `booking_history`
- `user_id`, `hotel_id`, `room_id` are external references — no real FK constraints across microservices.

> **Snapshot fields:** `hotel_name`, `room_type` and `price_per_night` are stored as snapshots at creation time. They do not change if the hotel or room data is updated later in ms-catalog. A booking is a contract at a point in time.

---

## How room_id and other data are populated

When a booking is created, `ms-booking` does **not** ask the client for all data. The flow is:

1. The client sends `roomId`, `hotelId`, `checkIn`, `checkOut` and `lockId`.
2. `ms-booking` calls `ms-catalog` via WebClient to fetch `pricePerNight`, `roomType` and `hotelName`.
3. `ms-booking` calls `ms-availability` to confirm the lock.
4. `ms-booking` computes `nights` and `totalPrice`, then persists the booking.
5. A `booking_history` entry is inserted with `previous_status: null` and `new_status: CONFIRMADA`.

This means `room_id` always comes from the client (the user already selected it in the availability search), while the rest of the room and hotel data is fetched from `ms-catalog` at creation time and stored as a snapshot.

---

## Booking Flow

```
Client → POST /api/bookings
           │
           ├── 1. WebClient → ms-catalog: GET /api/hotels/{hotelId}/rooms/{roomId}
           │       └── fetch: pricePerNight, roomType, hotelName
           │
           ├── 2. WebClient → ms-availability: PUT /api/availability/{roomId}/reserve
           │       └── confirm lock with lockId → get RESERVADA confirmation
           │
           ├── 3. Compute nights = checkOut - checkIn
           │        totalPrice = nights × pricePerNight
           │
           ├── 4. Persist booking with status CONFIRMADA
           │
           ├── 5. Insert booking_history row (null → CONFIRMADA)
           │
           └── 6. Publish booking.confirmed event to Kafka
```

---

## Endpoints

### 1. `POST /api/bookings`

**Access:** Authenticated users

**Description:** Creates and confirms a new booking. Requires a valid `lockId` from ms-availability.

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

**Response `201 Created`:**
```json
{
  "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "hotelName": "Grand Hotel Barcelona",
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "roomType": "DOBLE",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "nights": 5,
  "pricePerNight": 180.00,
  "totalPrice": 900.00,
  "status": "CONFIRMADA",
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

---

### 2. `GET /api/bookings/{id}`

**Access:** Authenticated users (own booking) / ADMIN (any booking)

**Description:** Returns the full detail of a booking. Regular users can only access their own bookings.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `200 OK`:**
```json
{
  "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "hotelName": "Grand Hotel Barcelona",
  "roomType": "DOBLE",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "nights": 5,
  "pricePerNight": 180.00,
  "totalPrice": 900.00,
  "status": "CONFIRMADA",
  "history": [
    {
      "previousStatus": null,
      "newStatus": "CONFIRMADA",
      "changedAt": "2025-01-15T10:30:00Z",
      "reason": null
    }
  ]
}
```

**Response `403 Forbidden`:**
```json
{
  "code": 403,
  "name": "FORBIDDEN",
  "description": "You do not have permission to access this booking",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 3. `GET /api/bookings/my`

**Access:** Authenticated users

**Description:** Returns a paginated list of all bookings for the authenticated user.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `status` | Enum | No | Filter by status (`CONFIRMADA`, `CANCELADA`) |
| `page` | Integer | No | Page number (default: 0) |
| `size` | Integer | No | Page size (default: 10) |

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
      "hotelName": "Grand Hotel Barcelona",
      "roomType": "DOBLE",
      "checkIn": "2025-03-10",
      "checkOut": "2025-03-15",
      "totalPrice": 900.00,
      "status": "CONFIRMADA"
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

**Description:** Cancels a confirmed booking. Calls ms-availability to release the room, updates the booking status to `CANCELADA`, inserts a `booking_history` row, and publishes a `booking.cancelled` event to Kafka.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `200 OK`:**
```json
{
  "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "status": "CANCELADA",
  "changedAt": "2025-01-16T09:00:00Z"
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

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "hotelName": "Grand Hotel Barcelona",
      "roomType": "DOBLE",
      "checkIn": "2025-03-10",
      "checkOut": "2025-03-15",
      "totalPrice": 900.00,
      "status": "CONFIRMADA"
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

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": "e5f6a7b8-c9d0-1234-ef01-345678901234",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "roomType": "DOBLE",
      "checkIn": "2025-03-10",
      "checkOut": "2025-03-15",
      "totalPrice": 900.00,
      "status": "CONFIRMADA"
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

| Topic | Trigger | Description |
|---|---|---|
| `booking.confirmed` | Booking created and confirmed | Triggers confirmation email in ms-notification |
| `booking.cancelled` | Booking cancelled | Triggers cancellation email in ms-notification |

### Example Payload — `booking.confirmed`

```json
{
  "eventType": "booking.confirmed",
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "userEmail": "johndoe@email.com",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "hotelName": "Grand Hotel Barcelona",
  "roomType": "DOBLE",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "totalPrice": 900.00,
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

## Error Format

| Field | Type | Description |
|---|---|---|
| `code` | Integer | HTTP status code |
| `name` | String | Short error identifier |
| `description` | String | Human-readable error message |
| `timestamp` | Instant | Time at which the error occurred |