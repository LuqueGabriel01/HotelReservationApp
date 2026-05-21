# 🏨 HotelApp — Hotel Reservation Platform

HotelApp is a web-based hotel reservation platform built with a **microservices architecture**. Users can search for available rooms, make and cancel reservations, and receive automatic email notifications. Administrators manage hotels, rooms, pricing, and photos from a dedicated panel.

---

## 📋 Project Overview

| Field | Details                                    |
|---|--------------------------------------------|
| **Project Name** | HotelApp — Hotel Reservation Platform      |
| **Architecture** | Microservices                              |
| **Backend** | Spring Boot 4.0.6 (Java)                   |
| **Database** | PostgreSQL (one database per microservice) |
| **Messaging** | Apache Kafka                               |
| **Image Storage** | Cloudinary                                 |
| **Security** | Spring Security + JWT                      |

---

## 🧩 Microservices

| Microservice | Port | Description |
|---|---|---|
| [ms-gateway](./docs/ms-gateway.md) | `8080` | Entry point — JWT validation and routing |
| [ms-auth](./docs/ms-auth.md) | `8081` | Registration, login and JWT issuance |
| [ms-catalog](./docs/ms-catalog.md) | `8082` | Hotel and room catalog, photo management |
| [ms-booking](./docs/ms-booking.md) | `8083` | Booking lifecycle management |
| [ms-availability](./docs/ms-availability.md) | `8084` | Room availability and concurrency control |
| [ms-notification](./docs/ms-notification.md) | `8085` | Email notifications via Kafka events |
| [ms-agent](./docs/ms-agent.md) | `8086` | AI-powered room recommender and chatbot |

---

## 🔗 Inter-Service Communication

```
ms-gateway        → routes all incoming requests to the appropriate service
ms-booking        → calls ms-availability to lock and confirm rooms
ms-availability   → called by ms-booking to manage room locks and releases
ms-booking        → publishes events to Kafka (booking.confirmed, booking.cancelled)
ms-notification   → consumes Kafka events and sends emails
All services      → validate JWT issued by ms-auth
```

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT |
| Database | PostgreSQL 18 |
| Cache | Redis |
| Messaging | Apache Kafka |
| Image Storage | Cloudinary |
| Containerization | Docker + Docker Compose |
| Build Tool | Maven |
| Frontend | Angular |

---

## ⚙️ Environment Variables

Copy the template file and fill in your values before starting the project:

```bash
cp .env.example .env
```

> [!IMPORTANT]
> Never commit the `.env` file. Make sure it is listed in `.gitignore`.

### `.env.example`

```env
# Global Configuration
SPRING_PROFILES_ACTIVE=dev

# Database Configuration
DATASOURCE_USERNAME=hotel_username
DATASOURCE_PASSWORD=hotel_password

# JPA Configuration
JPA_CONFIG_DDL=update
JPA_CONFIG_SHOW_SQL=true

# Swagger Configuration
UI_DOCUMENTATION_ENABLE=true

# Auth Service Database
AUTH_DATABASE_NAME=auth_db
AUTH_DATABASE_URL=jdbc:postgresql://localhost:5432/auth_db

# Catalog Service Database
CATALOG_DATABASE_NAME=catalog_db
CATALOG_DATABASE_URL=jdbc:postgresql://localhost:5432/catalog_db

# Availability Service Database
AVAILABILITY_DATABASE_NAME=availability_db
AVAILABILITY_DATABASE_URL=jdbc:postgresql://localhost:5432/availability_db

# Booking Service Database
BOOKING_DATABASE_NAME=booking_db
BOOKING_DATABASE_URL=jdbc:postgresql://localhost:5432/booking_db
```

### Variable Reference

| Variable | Description |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (`dev`, `prod`) |
| `DATASOURCE_USERNAME` | Shared PostgreSQL username |
| `DATASOURCE_PASSWORD` | Shared PostgreSQL password |
| `JPA_CONFIG_DDL` | Hibernate DDL mode (`update`, `create`, `validate`) |
| `JPA_CONFIG_SHOW_SQL` | Whether to log SQL queries (`true` / `false`) |
| `UI_DOCUMENTATION_ENABLE` | Enables Swagger UI on each service |
| `AUTH_DATABASE_NAME` | Database name for ms-auth |
| `CATALOG_DATABASE_NAME` | Database name for ms-catalog |
| `AVAILABILITY_DATABASE_NAME` | Database name for ms-availability |
| `BOOKING_DATABASE_NAME` | Database name for ms-booking |

---

## 🐳 Running the Project

### Prerequisites

- [Docker](https://www.docker.com/) and Docker Compose installed
- A valid [Cloudinary](https://cloudinary.com/) account (for photo uploads)

### Steps

1. Clone the repository:

```bash
git clone https://github.com/your-org/hotel-reservation-app.git
cd hotel-reservation-app
```

2. Set up environment variables:

```bash
cp .env.example .env
# Edit .env with your actual values
```

3. Start all services:

```bash
docker-compose up --build
```

4. To stop all services:

```bash
docker-compose down
```

---

## 🌐 Service URLs (local)

| Service | URL |
|---|---|
| Gateway | http://localhost:8080 |
| Auth | http://localhost:8081 |
| Catalog | http://localhost:8082 |
| Booking | http://localhost:8083 |
| Availability | http://localhost:8084 |
| Notification | http://localhost:8085 |
| Agent (AI) | http://localhost:8086 |

---

## 📄 Swagger UI

Each service exposes its API documentation at:

```
http://localhost:{PORT}/swagger-ui/index.html
```

> Swagger UI is only available when `UI_DOCUMENTATION_ENABLE=true` in your `.env`.

---

## 🗄️ Database Architecture

All microservices share a single PostgreSQL instance but use **isolated databases**, one per service:

| Service | Database |
|---|---|
| ms-auth | `auth_db` |
| ms-catalog | `catalog_db` |
| ms-availability | `availability_db` |
| ms-booking | `booking_db` |

Databases are initialized automatically on first startup via the `init.sh` script mounted in the PostgreSQL container.

---

## 📁 Project Structure

```
hotel-reservation-app/
├── ms-gateway/
├── ms-auth/
├── ms-catalog/
├── ms-booking/
├── ms-availability/
├── ms-notification/
├── ms-agent/
├── config/
│   └── database/
│       └── init.sh
├── docs/
│   ├── ms-gateway.md
│   ├── ms-auth.md
│   ├── ms-catalog.md
│   ├── ms-booking.md
│   ├── ms-availability.md
│   ├── ms-notification.md
│   └── ms-agent.md
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## 📚 Documentation

| Microservice | Description |
|---|---|
| [ms-gateway](./docs/ms-gateway.md) | Routing, JWT validation, rate limiting |
| [ms-auth](./docs/ms-auth.md) | User registration, login, JWT issuance |
| [ms-catalog](./docs/ms-catalog.md) | Hotels, rooms, photos via Cloudinary |
| [ms-booking](./docs/ms-booking.md) | Booking creation, consultation and cancellation |
| [ms-availability](./docs/ms-availability.md) | Availability checks and concurrency control |
| [ms-notification](./docs/ms-notification.md) | Kafka-driven email notifications |
| [ms-agent](./docs/ms-agent.md) | AI room recommender and customer chatbot |