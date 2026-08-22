# HotelApp

HotelApp is a hotel reservation platform built as a set of Spring Boot microservices behind a single gateway. Guests search availability, book and cancel rooms, and get emailed at each step; admins manage hotels, rooms, pricing and photos from a separate panel. An Angular frontend talks to the gateway; nothing else is exposed publicly.

Backend: Java 21, Spring Boot 4.0.6, Spring Security + JWT. Each service owns its own PostgreSQL database. Kafka carries booking events to the notification service, Redis backs rate limiting and AI session state, and Cloudinary stores hotel photos.

## Services

| Service | Port | Responsibility |
|---|---|---|
| [ms-gateway](./docs/ms-gateway.md) | 8080 | Single entry point: JWT validation, routing, rate limiting |
| [ms-auth](docs/ms-auth.md) | 8081 | Registration, login, JWT issuance and refresh |
| [ms-catalog](./docs/ms-catalog.md) | 8082 | Hotels, rooms and photos (Cloudinary) |
| [ms-booking](./docs/ms-booking.md) | 8083 | Booking lifecycle |
| [ms-availability](./docs/ms-availability.md) | 8084 | Room availability and locking |
| [ms-notification](./docs/ms-notification.md) | 8085 | Email notifications, driven by Kafka |
| [ms-agent](./docs/ms-agent.md) | 8086 | AI room recommendations and support chat (Gemini) |

Every request goes through the gateway, which validates the JWT issued by `ms-auth` and forwards it downstream with `X-User-Id` / `X-User-Role` headers, so the other services never have to decode a token themselves.

From there, the flow that matters most is booking creation: `ms-booking` reads room and pricing data from `ms-catalog`, confirms the lock with `ms-availability`, and once the booking is saved, publishes `booking.confirmed` to Kafka for `ms-notification` to pick up and email the guest. Cancellations and a daily check-in reminder job (which asks `ms-auth` for the guest's email) work the same way. `ms-agent` is the odd one out — it calls `ms-catalog` for room data and Gemini for the actual recommendation or chat reply, and keeps conversation state in Redis rather than a database.

## Configuration

Copy `.env.example` to `.env` and fill in real values before starting anything:

```bash
cp .env.example .env
```

`.env` is gitignored — don't commit it. The template groups variables by the service that owns them:

```env
# Global
SPRING_PROFILES_ACTIVE=dev
INTERNAL_API_KEY=your_internal_api_key
DATASOURCE_USERNAME=user
DATASOURCE_PASSWORD=user
JPA_CONFIG_DDL=update
JPA_CONFIG_SHOW_SQL=true
UI_DOCUMENTATION_ENABLE=true

# ms-auth
AUTH_DATABASE_NAME=auth_db
AUTH_DATABASE_URL=jdbc:postgresql://localhost:5432/auth_db
AUTH_SERVICE_URL=http://ms-auth:8081
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=3600
JWT_REFRESH_EXPIRATION=604800
JWT_TOKEN_TYPE=Bearer

# ms-catalog
CATALOG_DATABASE_NAME=catalog_db
CATALOG_DATABASE_URL=jdbc:postgresql://localhost:5432/catalog_db
CATALOG_SERVICE_URL=http://ms-catalog:8082
CLOUDINARY_CLOUD_NAME=product-environment-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret
HOTEL_CATALOG_CACHE_ENABLED=true
HOTEL_CATALOG_CACHE_TTL_MINUTES=10
HOTEL_CATALOG_CACHE_MAX_SIZE=500

# ms-availability
AVAILABILITY_DATABASE_NAME=availability_db
AVAILABILITY_DATABASE_URL=jdbc:postgresql://localhost:5432/availability_db
AVAILABILITY_SERVICE_URL=http://ms-availability:8084

# ms-booking
BOOKING_DATABASE_NAME=booking_db
BOOKING_DATABASE_URL=jdbc:postgresql://localhost:5432/booking_db
BOOKING_SERVICE_URL=http://ms-booking:8083

# ms-gateway
GATEWAY_ALLOWED_METHODS=GET,POST,PUT,DELETE
GATEWAY_ALLOWED_ORIGINS=http://localhost:4200
GATEWAY_RESPONSE_TIMEOUT=5s
GATEWAY_CONNECT_TIMEOUT=5000
GATEWAY_MAX_AGE=5000
GATEWAY_REPLENISH_RATE=10
GATEWAY_BURST_CAPACITY=2
GATEWAY_REQUEST_TOKEN=1

# Redis — rate limiting (ms-gateway) and session state (ms-agent)
REDIS_PORT=6379
REDIS_HOST=redis

# ms-agent
AGENT_SERVICE_URL=http://ms-agent:8086
GEMINI_API_KEY=your_gemini_api_key

# Kafka — produced by ms-booking, consumed by ms-notification
KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# ms-notification (Brevo SMTP relay)
EMAIL_EXTERNAL_KEY=your_smtp_key
EMAIL_SESSION=user_id@smtp-brevo.com
EMAIL_HOST=smtp-relay.brevo.com
EMAIL_PORT=587
```

`REDIS_HOST` and `KAFKA_BOOTSTRAP_SERVERS` need to resolve to the Docker Compose service names (`redis`, `kafka`) when running via `docker-compose up`. Point them at `localhost` only if you're running a service standalone against infrastructure exposed on the host.

A few groups worth knowing about:

- **JWT** (`JWT_SECRET`, `JWT_EXPIRATION`, `JWT_REFRESH_EXPIRATION`, `JWT_TOKEN_TYPE`) — the secret is shared between `ms-auth`, which signs tokens, and `ms-gateway`, which verifies them; both expirations are in seconds, and the refresh value also sets the refresh cookie's `Max-Age`.
- **`*_SERVICE_URL`** — the internal base URLs services use to call each other directly (auth, catalog, availability, booking, agent).
- **Gateway rate limiting** (`GATEWAY_REPLENISH_RATE`, `GATEWAY_BURST_CAPACITY`, `GATEWAY_REQUEST_TOKEN`) — token-bucket parameters backed by Redis.
- **Catalog cache** (`HOTEL_CATALOG_CACHE_*`) — in-memory cache for hotel/room reads.

## Running it

Requires Docker, Docker Compose, and a Cloudinary account for photo uploads.

```bash
git clone https://github.com/LuqueGabriel01/HotelReservationApp.git
cd HotelReservationApp
cp .env.example .env   # then edit .env with real values
docker-compose up --build
```

Stop everything with `docker-compose down`.

Local URLs:

| Service | URL |
|---|---|
| Gateway | http://localhost:8080 |
| Auth | http://localhost:8081 |
| Catalog | http://localhost:8082 |
| Booking | http://localhost:8083 |
| Availability | http://localhost:8084 |
| Notification | http://localhost:8085 |
| Agent (AI) | http://localhost:8086 |

Each service also serves Swagger at `http://localhost:{PORT}/swagger-ui/index.html` when `UI_DOCUMENTATION_ENABLE=true`.

## Databases

All services share one PostgreSQL instance but never share a schema — each gets its own database, created on first startup by `config/database/init.sh`:

| Service | Database |
|---|---|
| ms-auth | `auth_db` |
| ms-catalog | `catalog_db` |
| ms-availability | `availability_db` |
| ms-booking | `booking_db` |

`ms-notification` and `ms-agent` don't have one — notification is stateless and reacts only to Kafka events, and agent keeps its session state in Redis instead.

## Layout

```
HotelReservationApp/
├── ms-gateway/
├── ms-auth/
├── ms-catalog/
├── ms-booking/
├── ms-availability/
├── ms-notification/
├── ms-agent/
├── config/
│   ├── database/
│   │   └── init.sh
│   └── pmd/
│       └── ruleset.xml
├── docs/
│   ├── ms-gateway.md
│   ├── ms.auth.md
│   ├── ms-catalog.md
│   ├── ms-booking.md
│   ├── ms-availability.md
│   ├── ms-notification.md
│   └── ms-agent.md
├── hotel-reservation-data/   # Postgres data volume, gitignored
├── docker-compose.yml
├── .env.example
└── README.md
```

## Docs

Full endpoint reference, database schema and implementation notes for each service live in `docs/`:

[ms-gateway](./docs/ms-gateway.md) · [ms-auth](docs/ms-auth.md) · [ms-catalog](./docs/ms-catalog.md) · [ms-booking](./docs/ms-booking.md) · [ms-availability](./docs/ms-availability.md) · [ms-notification](./docs/ms-notification.md) · [ms-agent](./docs/ms-agent.md)
