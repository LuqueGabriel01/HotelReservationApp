# 📅 ms-availability — Availability Service

[← Back to README](../README.md)

**Port:** `8084`  
**Database:** PostgreSQL (`availability_db`)

The `ms-availability` service is the core of the business logic. It determines which rooms are free between two dates and manages temporary locks during the booking process, ensuring that two users cannot reserve the same room at the same time.

---

## Database — `availability_db`

### Table: `room_availability`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | UUID | PK | Unique identifier |
| `room_id` | UUID | NOT NULL | Room reference (external — from ms-catalog) |
| `date` | DATE | NOT NULL | A single blocked or reserved date |
| `status` | ENUM | NOT NULL | Current status of the date |
| `booking_id` | UUID | nullable | Booking reference if status is RESERVADA |

### Table: `room_blocks`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | UUID | PK | Unique identifier (used as `lockId`) |
| `room_id` | UUID | NOT NULL | Room being blocked (external reference) |
| `user_id` | UUID | NOT NULL | User who initiated the block (external reference) |
| `check_in` | DATE | NOT NULL | Check-in date of the intended booking |
| `check_out` | DATE | NOT NULL | Check-out date of the intended booking |
| `expires_at` | TIMESTAMP | NOT NULL | When the temporary block expires (default: +10 min) |

### Enums

```
AvailabilityStatus: BLOQUEADA, RESERVADA
```

> **No DISPONIBLE status.** A room is considered available when there are **no records** in `room_availability` for those dates. Only occupied dates are stored, which keeps the table lightweight.

### Relationships

No real foreign keys between tables — `room_id`, `user_id` and `booking_id` are logical references to other microservices. No cross-service FK constraints.

---

## How availability is modelled per day

Each row in `room_availability` represents **one blocked or reserved date** for a room. For a booking from March 10 to March 15, five rows are inserted (one per night: March 10, 11, 12, 13, 14). This approach makes querying specific dates very efficient and avoids complex date range overlap logic.

---

## Endpoints

### 1. `GET /api/availability`

**Access:** Public

**Description:** Returns available rooms for a hotel between two dates. A room is available if it has no `BLOQUEADA` or `RESERVADA` entries for any date in the requested range.

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `hotelId` | UUID | Yes | Hotel to search in |
| `checkIn` | Date | Yes | Check-in date (`YYYY-MM-DD`) |
| `checkOut` | Date | Yes | Check-out date (`YYYY-MM-DD`) |

**Response `200 OK`:**
```json
[
  {
    "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
    "type": "DOBLE",
    "capacity": 2,
    "pricePerNight": 180.00,
    "available": true
  }
]
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

### 2. `GET /api/availability/{roomId}`

**Access:** Public

**Description:** Returns availability status for a specific room and date range.

**Response `200 OK`:**
```json
{
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "available": false,
  "reason": "RESERVADA"
}
```

---

### 3. `POST /api/availability/{roomId}/block`

**Access:** Authenticated users

**Description:** Temporarily locks a room by creating a `room_blocks` entry. Uses pessimistic locking to prevent race conditions. Returns a `lockId` the client must send when creating the booking.

**Headers:**
```
Authorization: Bearer <token>
```

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
  "expiresAt": "2025-01-15T10:40:00Z"
}
```

**Response `409 Conflict`:**
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

**Description:** Manually releases a temporary block when the user abandons the booking process.

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `lockId` | UUID | Yes | Lock identifier returned on block creation |

**Response `204 No Content`**

---

### 5. `PUT /api/availability/{roomId}/reserve` *(internal)*

**Access:** Internal — called by ms-booking only, not exposed through the gateway.

**Description:** Converts the `room_blocks` entry into permanent `room_availability` rows (one per night) with status `RESERVADA`. Called after a booking is confirmed.

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
  "status": "RESERVADA"
}
```

**Response `404 Not Found`** — when the lock has expired:
```json
{
  "code": 404,
  "name": "NOT_FOUND",
  "description": "Lock has expired or does not exist. Please restart the booking process.",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 6. `PUT /api/availability/{roomId}/release` *(internal)*

**Access:** Internal — called by ms-booking only, not exposed through the gateway.

**Description:** Deletes all `room_availability` rows for a given booking, making the room available again. Called when a booking is cancelled.

**Request Body:**
```json
{
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234"
}
```

**Response `200 OK`:**
```json
{
  "released": true,
  "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "bookingId": "e5f6a7b8-c9d0-1234-ef01-345678901234"
}
```

---

## Scheduled Jobs

| Job | Schedule | Action |
|---|---|---|
| `StaleBlockCleanupJob` | Every 60 seconds | Deletes `room_blocks` entries whose `expires_at` is in the past |

---

## Error Format

| Field | Type | Description |
|---|---|---|
| `code` | Integer | HTTP status code |
| `name` | String | Short error identifier |
| `description` | String | Human-readable error message |
| `timestamp` | Instant | Time at which the error occurred |