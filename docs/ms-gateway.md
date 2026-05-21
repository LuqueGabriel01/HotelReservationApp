# 🌐 ms-gateway — API Gateway

[← Back to README](../README.md)

**Port:** `8080`  
**Cache:** Redis

The `ms-gateway` is the single entry point for all client requests. It receives every request from the Angular frontend, validates the JWT token, and routes the request to the appropriate microservice. No microservice is ever contacted directly by the frontend.

---

## Responsibilities

- **Routing** — forwards incoming requests to the correct downstream service based on the path prefix.
- **JWT Validation** — verifies the Bearer token on every protected route before forwarding. Requests with missing or invalid tokens are rejected immediately.
- **Rate Limiting** — uses Redis to track request counts per client IP and reject traffic that exceeds the configured threshold.
- **CORS** — handles cross-origin configuration for the Angular frontend.

---

## Routing Table

| Path Prefix | Forwarded To | Port |
|---|---|---|
| `/api/auth/**` | ms-auth | `8081` |
| `/api/hotels/**` | ms-catalog | `8082` |
| `/api/bookings/**` | ms-booking | `8083` |
| `/api/availability/**` | ms-availability | `8084` |
| `/api/ai/**` | ms-agent | `8086` |

---

## Authentication Flow

```
Client → ms-gateway
           │
           ├── Is the route public? (e.g. /api/auth/login, /api/auth/register)
           │        └── YES → forward request directly
           │
           └── NO → validate Authorization: Bearer <token>
                        │
                        ├── Token invalid or missing → 401 Unauthorized
                        │
                        └── Token valid → extract userId and role
                                             └── forward request with enriched headers
```

The gateway injects the following headers into every forwarded request so downstream services do not need to decode the JWT themselves:

| Header | Description |
|---|---|
| `X-User-Id` | UUID of the authenticated user |
| `X-User-Role` | Role of the authenticated user (`USER` or `ADMIN`) |

---

## Rate Limiting

Rate limiting is enforced per client IP using Redis as the counter store.

| Parameter | Default Value |
|---|---|
| Requests allowed | 100 per minute |
| Window | 60 seconds |
| Rejection response | `429 Too Many Requests` |

**Response `429 Too Many Requests`:**
```json
{
  "code": 429,
  "name": "TOO_MANY_REQUESTS",
  "description": "Rate limit exceeded. Please try again later.",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

## Public Routes

The following routes bypass JWT validation:

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | User registration |
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/refresh` | Token refresh |
| GET | `/api/hotels` | List hotels |
| GET | `/api/hotels/{id}` | Hotel detail |
| GET | `/api/hotels/{id}/rooms` | List rooms |
| GET | `/api/hotels/{id}/rooms/{roomId}` | Room detail |

All other routes require a valid Bearer token.

---

## Error Responses

| Code | Name | Description |
|---|---|---|
| `401` | `UNAUTHORIZED` | Missing or invalid JWT token |
| `403` | `FORBIDDEN` | Valid token but insufficient role |
| `429` | `TOO_MANY_REQUESTS` | Rate limit exceeded |
| `503` | `SERVICE_UNAVAILABLE` | Downstream service is unreachable |

**Response `401 Unauthorized`:**
```json
{
  "code": 401,
  "name": "UNAUTHORIZED",
  "description": "Missing or invalid authorization token",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

**Response `503 Service Unavailable`:**
```json
{
  "code": 503,
  "name": "SERVICE_UNAVAILABLE",
  "description": "The requested service is temporarily unavailable",
  "timestamp": "2025-01-15T10:30:00Z"
}
```