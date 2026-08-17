# 🏨 ms-catalog — Hotel Service

[← Back to README](../README.md)

**Port:** `8082`  
**Database:** PostgreSQL (`catalog_db`)  
**External:** Cloudinary (image storage)

The `ms-catalog` service acts as the application's catalog. It manages all hotel and room data, including descriptions, pricing, capacity, and photos. Image uploads are handled through Cloudinary.

---

## Database — `catalog_db`

### Table: `hotels`

| Column        | Type      | Constraints   | Description        |
|---------------|-----------|---------------|--------------------|
| `id`          | UUID      | PK            | Unique identifier  |
| `name`        | VARCHAR   | NOT NULL      | Hotel name         |
| `description` | TEXT      |               | Full description   |
| `address`     | VARCHAR   | NOT NULL      | Physical address   |
| `city`        | VARCHAR   | NOT NULL      | City               |
| `stars`       | INT       | NOT NULL, 1–5 | Star rating        |
| `created_at`  | TIMESTAMP | NOT NULL      | Creation timestamp |

### Table: `rooms`

| Column            | Type    | Constraints           | Description              |
|-------------------|---------|-----------------------|--------------------------|
| `id`              | UUID    | PK                    | Unique identifier        |
| `hotel_id`        | UUID    | FK → hotels, NOT NULL | Hotel it belongs to      |
| `type`            | ENUM    | NOT NULL              | Room type                |
| `price_per_night` | DECIMAL | NOT NULL              | Price per night          |
| `capacity`        | INT     | NOT NULL              | Maximum number of guests |
| `description`     | TEXT    |                       | Room description         |

### Table: `hotel_images`

| Column     | Type    | Constraints             | Description                  |
|------------|---------|-------------------------|------------------------------|
| `id`       | UUID    | PK                      | Unique identifier            |
| `hotel_id` | UUID    | FK → hotels, NOT NULL   | Hotel it belongs to          |
| `url`      | VARCHAR | NOT NULL                | Cloudinary public URL        |
| `is_main`  | BOOLEAN | NOT NULL, default false | Whether it is the main image |

### Table: `amenities`

| Column | Type    | Constraints      | Description                         |
|--------|---------|------------------|-------------------------------------|
| `id`   | UUID    | PK               | Unique identifier                   |
| `name` | VARCHAR | UNIQUE, NOT NULL | Amenity name (wifi, pool, parking…) |

### Table: `hotel_amenities` *(pivot — N:M)*

| Column       | Type | Constraints        | Description |
|--------------|------|--------------------|-------------|
| `hotel_id`   | UUID | FK → hotels, PK    | Hotel       |
| `amenity_id` | UUID | FK → amenities, PK | Amenity     |

### Enums

```
RoomType: SIMPLE, DOBLE, SUITE
```

### Relationships

- `hotels` 1 → N `rooms`
- `hotels` 1 → N `hotel_images`
- `hotels` N ↔ N `amenities` via `hotel_amenities`

---

## Endpoints

### 1. `GET /api/hotels`

**Access:** Public

**Description:** Returns a paginated list of all hotels. Supports optional filtering by city.

**Query Parameters:**

| Parameter   | Type         | Required | Description              |
|-------------|--------------|----------|--------------------------|
| `city`      | String       | No       | Filter by city           |
| `stars`     | Integer      | No       | Filter by stars (1-5)    |
| `amenities` | List<String> | No       | Filter by amenity names  |
| `page`      | Integer      | No       | Page number (default: 0) |
| `size`      | Integer      | No       | Page size (default: 10)  |

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "name": "Grand Hotel Barcelona",
      "description": "A luxury hotel in the heart of Barcelona.",
      "address": "Carrer de Pau Claris, 122",
      "city": "Barcelona",
      "stars": 5,
      "mainImage": "https://res.cloudinary.com/demo/image/upload/hotel1.jpg",
      "amenities": ["wifi", "pool", "parking"]
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

---

### 2. `GET /api/hotels/{id}`

**Access:** Public

**Description:** Returns the full detail of a single hotel including all images and amenities.

**Response `200 OK`:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Grand Hotel Barcelona",
  "description": "A luxury hotel in the heart of Barcelona.",
  "address": "Carrer de Pau Claris, 122",
  "city": "Barcelona",
  "stars": 5,
  "images": [
    { "id": "f1e2d3c4-b5a6-7890-abcd-ef1234567890", "url": "https://res.cloudinary.com/demo/image/upload/hotel1.jpg", "isMain": true }
  ],
  "amenities": ["wifi", "pool", "parking"],
  "createdAt": "2025-01-10T09:00:00Z"
}
```

**Response `404 Not Found`:**
```json
{
  "code": 404,
  "name": "NOT_FOUND",
  "description": "Hotel with id a1b2c3d4-e5f6-7890-abcd-ef1234567890 not found",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 3. `POST /api/hotels`

**Access:** ADMIN only

**Description:** Creates a new hotel.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "name": "Grand Hotel Barcelona",
  "description": "A luxury hotel in the heart of Barcelona.",
  "address": "Carrer de Pau Claris, 122",
  "city": "Barcelona",
  "stars": 5,
  "amenities": ["wifi", "pool"]
}
```

**Response `201 Created`:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Grand Hotel Barcelona",
  "city": "Barcelona",
  "stars": 5,
  "createdAt": "2025-01-15T10:30:00Z"
}
```

---

### 4. `PUT /api/hotels/{id}`

**Access:** ADMIN only

**Description:** Updates an existing hotel. All fields are optional.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "name": "Grand Hotel Barcelona Updated",
  "stars": 4
}
```

**Response `200 OK`:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Grand Hotel Barcelona Updated",
  "stars": 4
}
```

---

### 5. `DELETE /api/hotels/{id}`

**Access:** ADMIN only

**Description:** Deletes a hotel and all its associated rooms and images. Images are also deleted from Cloudinary.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `204 No Content`**

---

### 6. `GET /api/hotels/{id}/rooms`

**Access:** Public

**Description:** Returns all rooms belonging to a specific hotel.

**Query Parameters:**

| Parameter   | Type       | Required | Description                   |
|-------------|------------|----------|-------------------------------|
| `capacity`  | Integer    | No       | Filter by capacity            |
| `type`      | String     | No       | Filter by room type)          |
| `Min price` | BigDecimal | No       | Filter by minimal price range |
| `Max price` | BigDecimal | No       | Filter by maximum price range |

**Response `200 OK`:**
```json
[
  {
    "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
    "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "type": "DOUBLE",
    "description": "Spacious room with sea views.",
    "capacity": 2,
    "pricePerNight": 180.00
  }
]
```

---

### 7. `GET /api/hotels/{id}/rooms/{roomId}`

**Access:** Public

**Description:** Returns the full detail of a specific room.

**Response `200 OK`:**
```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "type": "DOUBLE",
  "description": "Spacious room with sea views.",
  "capacity": 2,
  "pricePerNight": 180.00
}
```

---

### 8. `POST /api/hotels/{id}/rooms`

**Access:** ADMIN only

**Description:** Creates a new room within a hotel.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "type": "DOUBLE",
  "description": "Spacious room with sea views.",
  "capacity": 2,
  "pricePerNight": 180.00
}
```

**Response `201 Created`:**
```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "type": "DOUBLE",
  "capacity": 2,
  "pricePerNight": 180.00
}
```

---

### 9. `PUT /api/hotels/{id}/rooms/{roomId}`

**Access:** ADMIN only

**Description:** Updates an existing room. All fields are optional.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "pricePerNight": 200.00
}
```

**Response `200 OK`:**
```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "pricePerNight": 200.00
}
```

---

### 10. `DELETE /api/hotels/{id}/rooms/{roomId}`

**Access:** ADMIN only

**Description:** Deletes a room.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `204 No Content`**

---

### 11. `POST /api/hotels/{id}/photos`

**Access:** ADMIN only

**Description:** Uploads one or more photos for a hotel. Stored in Cloudinary. All the photos uploaded will have `isMain: false` by default.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Response `201 Created`:**
```json
[
  {
    "id": "f1e2d3c4-b5a6-7890-abcd-ef1234567890",
    "url": "https://res.cloudinary.com/demo/image/upload/hotel1.jpg",
    "isMain": false
  }
]
```

---

### 12. `PATCH /api/hotels/{id}/photos/{iamgeId}/main`

**Access:** ADMIN only

**Description:** Set a specific image from a hotel to be the main one.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `200 OK`:**
```json
[
  {
    "id": "f1e2d3c4-b5a6-7890-abcd-ef1234567890",
    "url": "https://res.cloudinary.com/demo/image/upload/hotel1.jpg",
    "isMain": true
  }
]
```

---

### 13. `DELETE /api/hotels/{id}/photos/{iamgeId}`

**Access:** ADMIN only

**Description:** Deletes a photo from both the database and Cloudinary.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `204 No Content`**

---

### 14. `GET /api/hotels/{id}/rooms/{roomId}/booking(internal)`

**Access:** Internal — called by ms-booking only, not exposed through the gateway.

**Description:** Returns data requested belonging to a specific hotel and room for ms-booking.

**Response `200 OK`:**
```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "hotelName" : "Barcelona Eurostars",
  "type": "DOUBLE",
  "description": "Spacious room with sea views.",
  "capacity": 2,
  "pricePerNight": 180.00
}
```

---

## Error Format

| Field         | Type    | Description                      |
|---------------|---------|----------------------------------|
| `code`        | Integer | HTTP status code                 |
| `name`        | String  | Short error identifier           |
| `description` | String  | Human-readable error message     |
| `timestamp`   | Instant | Time at which the error occurred |