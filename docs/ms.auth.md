# 🟣 ms-auth — Authentication Service

[← Back to README](../README.md)

**Port:** `8081`  
**Database:** PostgreSQL (`auth_db`)

The `ms-auth` service manages user registration and authentication. It is responsible for issuing JWT tokens that all other domain microservices must validate on every protected request.

---

## Database — `auth_db`

### Table: `users`

| Column       | Type      | Constraints | Description                 |
|--------------|-----------|---|-----------------------------|
| `id`         | UUID      | PK | Unique identifier           |
| `name`       | VARCHAR   | NOT NULL | Full name of the user       |
| `email`      | VARCHAR   | UNIQUE, NOT NULL | Email address               |
| `password`   | VARCHAR   | NOT NULL | BCrypt hash of the password |
| `enabled`    | BOOLEAN   | NOT NULL | Account status (Default: true)       |
| `role`       | ENUM      | NOT NULL | User role                   |
| `created_at` | TIMESTAMP | NOT NULL | Registration timestamp      |
| `updated_at` | TIMESTAMP | NOT NULL | Update timestamp            |

### Table: `tokens`

| Column       | Type   | Constraints            | Description                 |
|--------------|--------|------------------------|-----------------------------|
| `id`         | UUID   | PK                     | Unique identifier (Auto-generated)           |
| `token`      | TEXT   | NOT NULL               | Authentication token string      |
| `token_type` | ENUM   | UNIQUE, NOT NULL       | Type of token (Default: 'BEARER')              |
| `revoked`    | BOOLEAN | NOT NULL               | Indicates if the token was revoked |
| `expired`    | BOOLEAN   | NOT NULL               | Indicates if the token has expired                |
| `created_at` | TIMESTAMP | NOT NULL               | Generation timestamp (Auto)    |
| `closed_at`  | TIMESTAMP | NOT NULL               | Session closure timestamp         |
| `user_id`    | UUID | FK(users.id), NOT NULL | Reference to the user owner           |

### Enums

```
UserRole: ROLE_USER, ROLE_ADMIN
```

> This is the only table in `auth_db`. No foreign keys — it is a fully independent database.

---

## Endpoints

### 1. `POST /api/auth/register`

**Access:** Public

**Description:** Registers a new user in the system.

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "johndoe@email.com",
  "password": "SecurePass1"
}
```

**Validation Rules:**
- `email` must be unique and in a valid format.
- `password` must be at least 8 characters, contain at least one uppercase letter and one number.
- `username` must be unique and contain no spaces.

**Response `201 Created`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "johndoe",
  "email": "johndoe@email.com",
  "tokens": {
    "accessToken": "eyJheGtiOiJqUzUxMiJ9...",
    "refreshToken": "eyJrqGfiOiJIwzUxMiJ9..."
  },
  "role": "ROLE_USER",
  "createdAt": "2025-01-15T10:30:00Z"
}
```

**Response `409 Conflict`** — when the email or username is already taken:
```json
{
  "code": 409,
  "name": "CONFLICT",
  "description": "A user with that email already exists",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

**Response `400 Bad Request`** — when validation fails:
```json
{
  "code": 400,
  "name": "BAD_REQUEST",
  "description": "Password must be at least 8 characters and contain one uppercase letter and one number",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 2. `POST /api/auth/login`

**Access:** Public

**Description:** Authenticates a user and returns a JWT access token along with a refresh token.

**Request Body:**
```json
{
  "email": "johndoe@email.com",
  "password": "SecurePass1"
}
```

**Response `200 OK`:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**Response `401 Unauthorized`** — when credentials are invalid:
```json
{
  "code": 401,
  "name": "UNAUTHORIZED",
  "description": "Invalid email or password",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 3. `POST /api/auth/logout`

**Access:** Authenticated users

**Description:** Invalidates the current session by blacklisting the JWT token.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `204 No Content`**

**Response `401 Unauthorized`** — when no valid token is provided:
```json
{
  "code": 401,
  "name": "UNAUTHORIZED",
  "description": "Missing or invalid authorization token",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 4. `POST /api/auth/refresh`

**Access:** Public (requires a valid refresh token)

**Description:** Issues a new access token using a valid refresh token, without requiring the user to log in again.

**Request Body:**
```json
{
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4..."
}
```

**Response `200 OK`:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**Response `401 Unauthorized`** — when the refresh token is expired or invalid:
```json
{
  "code": 401,
  "name": "UNAUTHORIZED",
  "description": "Refresh token is expired or invalid",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 5. `GET /api/auth/me`

**Access:** Authenticated users

**Description:** Returns the profile information of the currently authenticated user.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `200 OK`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "johndoe",
  "email": "johndoe@email.com",
  "role": "ROLE_USER",
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-15T10:30:00Z"
}
```

**Response `401 Unauthorized`** — when no valid token is provided:
```json
{
  "code": 401,
  "name": "UNAUTHORIZED",
  "description": "Missing or invalid authorization token",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 6. `PUT /api/auth/me`

**Access:** Authenticated users

**Description:** Updates the profile information of the currently authenticated user. All fields are optional; only the provided fields will be updated.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "username": "johndoe_updated",
  "email": "newemail@email.com",
  "password": "NewSecurePass1"
}
```

**Validation Rules:**
- Same rules as registration apply to any field that is provided.
- `email` and `username` must remain unique across the system.

**Response `200 OK`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "johndoe_updated",
  "email": "newemail@email.com",
  "role": "ROLE_USER",
  "tokens": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "updatedAt": "2025-01-16T09:00:00Z"
}
```

**Response `409 Conflict`** — when the new email or username is already taken:
```json
{
  "code": 409,
  "name": "CONFLICT",
  "description": "A user with that username already exists",
  "timestamp": "2025-01-16T09:00:00Z"
}
```

---

## Error Format

All error responses follow a consistent structure across the service:

| Field | Type | Description |
|---|---|---|
| `code` | Integer | HTTP status code |
| `name` | String | Short error identifier |
| `description` | String | Human-readable error message |
| `timestamp` | Instant | Time at which the error occurred |

---

## JWT Token Details

| Property | Value |
|---|---|
| Algorithm | HS256 |
| Access token expiry | 1 hour |
| Refresh token expiry | 7 days |
| Token type | Bearer |

The JWT payload includes the following claims:

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "role": "ROLE_USER",
  "iat": 1736934600,
  "exp": 1736938200
}
```