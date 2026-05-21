# 🤖 ms-agent — AI Service

[← Back to README](../README.md)

**Port:** `8086`  
**External:** OpenAI API

The `ms-agent` service connects the application to OpenAI's API. It offers two features: a room recommender based on user preferences, and a customer support chatbot that answers frequently asked questions about the hotel.

---

## Endpoints

### 1. `POST /api/ai/recommend`

**Access:** Authenticated users

**Description:** Recommends available rooms based on the user's stated preferences (number of guests, budget, desired dates, special requirements). The service queries `ms-catalog` to retrieve current room data and sends it to OpenAI along with the user's preferences to generate a ranked list of recommendations with explanations.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "checkIn": "2025-03-10",
  "checkOut": "2025-03-15",
  "guests": 2,
  "budget": 250.00,
  "preferences": "We prefer a quiet room with a good view. We do not need a kitchen."
}
```

**Validation Rules:**
- `hotelId`, `checkIn`, `checkOut`, and `guests` are required.
- `checkOut` must be after `checkIn`.
- `guests` must be a positive integer.
- `budget` and `preferences` are optional but improve recommendation quality.

**Response `200 OK`:**
```json
{
  "recommendations": [
    {
      "roomId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "roomName": "Deluxe Double Room",
      "pricePerNight": 180.00,
      "capacity": 2,
      "score": 1,
      "reason": "This room fits your group size perfectly and falls within your budget. It is located on the upper floors with panoramic views, ideal for guests seeking a quiet atmosphere."
    },
    {
      "roomId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
      "roomName": "Superior Suite",
      "pricePerNight": 320.00,
      "capacity": 4,
      "score": 2,
      "reason": "Slightly above your stated budget, but offers a private terrace with exceptional views. Recommended if you are open to a premium experience."
    }
  ]
}
```

**Response `404 Not Found`** — when the hotel does not exist or has no available rooms:
```json
{
  "code": 404,
  "name": "NOT_FOUND",
  "description": "No available rooms found for the given hotel and dates",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

**Response `503 Service Unavailable`** — when the OpenAI API is unreachable:
```json
{
  "code": 503,
  "name": "SERVICE_UNAVAILABLE",
  "description": "The AI service is temporarily unavailable. Please try again later.",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 2. `POST /api/ai/chat`

**Access:** Public

**Description:** Provides a conversational customer support chatbot. Users can ask questions about hotel policies, services, check-in times, amenities, and other frequently asked questions. The conversation history is maintained within a single session using a `sessionId`.

**Request Body:**
```json
{
  "sessionId": "sess-abc123",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "message": "What time is check-in? Is early check-in available?"
}
```

**Validation Rules:**
- `message` and `hotelId` are required.
- `sessionId` is optional on the first message; if omitted, a new session is created and its ID is returned in the response.

**Response `200 OK`:**
```json
{
  "sessionId": "sess-abc123",
  "reply": "Check-in time at Grand Hotel Barcelona is 3:00 PM. Early check-in may be available depending on room availability on the day of arrival. We recommend contacting the hotel directly to arrange it in advance.",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

**Response `400 Bad Request`** — when the message is empty:
```json
{
  "code": 400,
  "name": "BAD_REQUEST",
  "description": "Message field must not be empty",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

**Response `503 Service Unavailable`** — when the OpenAI API is unreachable:
```json
{
  "code": 503,
  "name": "SERVICE_UNAVAILABLE",
  "description": "The AI service is temporarily unavailable. Please try again later.",
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