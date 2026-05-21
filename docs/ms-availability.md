# 📅 ms-availability — Availability Service

[← Back to README](../README.md)

**Port:** `8083`  
**Database:** PostgreSQL (`availability_db`)

The `ms-availability` service is the core of the business logic. It determines which rooms are free between two dates and manages temporary locks during the booking process, ensuring that two users cannot reserve the same room at the same time.

---

## Data Model

### Entity: `RoomAvailability`

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `roomId` | UUID | Reference to the room (from ms-hotel) |
| `checkIn` | LocalDate | Start date of the occupied period |
| `checkOut` | LocalDate | End date of the occupied period |
| `status` | Enum | Current status of the period |
| `bookingId` | UUID | Reference to the booking (nullable, set on confirmation) |
| `createdAt` | Instant | Creation timestamp |
| `updatedAt` | Instant | Last update timestamp |

### Enums

```
AvailabilityStatus: BLOCKED, RESERVED
```

> **Note:** `BLOCKED` is a temporary lock set during the booking flow. It expires automatically if not confirmed within a configured time window (default: 10 minutes). `RESERVED` is a permanent lock set once a booking is confirmed.

---

## Endpoints

### 1. `GET /api/availability`

**Access:** Public

**Description:** Returns a list of available rooms for a given hotel between two dates. A room is considered available if it has no `BLOCKED` or `RESERVED` entries overlapping with the requested period.

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `hotelId` | UUID | Yes | Hotel to search in |
| `checkIn` | LocalDate | Yes | Check-in date (`YYYY-MM-DD`) |
| `checkOut` | LocalDate | Yes | Check-out date (`YYYY-MM-DD`) |

**Response `200 OK`:**
```json
[
  {
    "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
    "name": "Deluxe Double Room",
    "capacity": 2,
    "pricePerNight": 180.00,
    "available": true
  },
  {
    "roomId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
    "name": "Superior Suite",
    "capacity": 4,
    "pricePerNight": 320.00,
    "available": true
  }
]
```

**Response `400 Bad Request`** — when dates are invalid:
```json
{
  "code": 400,
  "name": "BAD_REQUEST",
  "description": "checkOut date must be after checkIn date",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 2. `GET /api/availability/{roomId}`

**Access:** Public

**Description:** Returns the availability status of a specific room for a given date range.

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `roomId` | UUID | Room identifier |

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `checkIn` | LocalDate | Yes | Check-in date (`YYYY-MM-DD`) |
| `checkOut` | LocalDate | Yes | Check-out date (`YYYY-MM-DD`) |

**Response `200 OK`:**
```json
{
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "available": true
}
```

**Response `200 OK`** — when the room is not available:
```json
{
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "available": false,
  "reason": "RESERVED"
}
```

---

### 3. `POST /api/availability/{roomId}/block`

**Access:** Authenticated users

**Description:** Temporarily locks a room for a given date range while the user completes the booking process. If the booking is not confirmed within the lock window (default: 10 minutes), the lock expires automatically and the room becomes available again.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `roomId` | UUID | Room identifier |

**Request Body:**
```json
{
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15"
}
```

**Response `200 OK`:**
```json
{
  "lockId": "d4e5f6a7-b8c9-0123-def0-234567890123",
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "status": "BLOCKED",
  "expiresAt": "2025-01-15T10:40:00Z"
}
```

**Response `409 Conflict`** — when the room is already blocked or reserved:
```json
{
  "code": 409,
  "name": "CONFLICT",
  "description": "Room is not available for the selected dates",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 4. `DELETE /api/availability/{roomId}/block`

**Access:** Authenticated users

**Description:** Manually releases a temporary block on a room, making it available again. This is called when a user abandons the booking process.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `roomId` | UUID | Room identifier |

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `lockId` | UUID | Yes | Lock identifier returned when the block was created |

**Response `204 No Content`**

**Response `404 Not Found`** — when the lock does not exist or has already expired:
```json
{
  "code": 404,
  "name": "NOT_FOUND",
  "description": "Active lock not found for the given roomId and lockId",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 5. `PUT /api/availability/{roomId}/reserve`

**Access:** Internal (called by ms-booking)

**Description:** Converts a temporary block into a permanent reservation after a booking has been confirmed. This endpoint is intended for internal service-to-service communication only and is not exposed through the API Gateway.

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `roomId` | UUID | Room identifier |

**Request Body:**
```json
{
  "lockId": "d4e5f6a7-b8c9-0123-def0-234567890123",
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234"
}
```

**Response `200 OK`:**
```json
{
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "status": "RESERVED"
}
```

**Response `404 Not Found`** — when the lock has expired or does not exist:
```json
{
  "code": 404,
  "name": "NOT_FOUND",
  "description": "Lock has expired or does not exist. Please restart the booking process.",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 6. `PUT /api/availability/{roomId}/release`

**Access:** Internal (called by ms-booking)

**Description:** Releases a confirmed reservation, making the room available again. This is called when a booking is cancelled. This endpoint is intended for internal service-to-service communication only.

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `roomId` | UUID | Room identifier |

**Request Body:**
```json
{
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234"
}
```

**Response `200 OK`:**
```json
{
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234",
  "status": "RELEASED"
}
```

**Response `404 Not Found`:**
```json
{
  "code": 404,
  "name": "NOT_FOUND",
  "description": "No active reservation found for bookingId e5f6a7b8-c9d0-1234-ef01-345678901234",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

## Concurrency Handling

To prevent double bookings, this service applies a **pessimistic locking** strategy at the database level when checking and setting availability. The flow is:

1. User initiates a booking → `POST /api/availability/{roomId}/block` is called.
2. The service acquires a row-level lock and checks for overlapping entries.
3. If the room is free, a `BLOCKED` entry is created with an expiry timestamp.
4. If the booking is confirmed within the time window → `PUT /api/availability/{roomId}/reserve` converts the block to `RESERVED`.
5. If the time window expires without confirmation, a scheduled job automatically removes stale `BLOCKED` entries.

---

## Error Format

All error responses follow a consistent structure:

| Field | Type | Description |
|---|---|---|
| `code` | Integer | HTTP status code |
| `name` | String | Short error identifier |
| `description` | String | Human-readable error message |
| `timestamp` | Instant | Time at which the error occurred |