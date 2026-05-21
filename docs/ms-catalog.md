# 🏨 ms-catalog — Catalog Service

[← Back to README](../README.md)

**Port:** `8082`  
**Database:** PostgreSQL (`catalog_db`)  
**External:** Cloudinary (image storage)

The `ms-catalog` service acts as the application's catalog. It manages all hotel and room data, including descriptions, pricing, capacity, and photos. Image uploads are handled through Cloudinary.

---

## Data Model

### Entity: `Hotel`

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `name` | String | Hotel name |
| `description` | String | Full description of the hotel |
| `address` | String | Physical address |
| `city` | String | City where the hotel is located |
| `country` | String | Country where the hotel is located |
| `stars` | Integer | Star rating (1–5) |
| `photos` | List<Photo> | List of hotel photos |
| `createdAt` | Instant | Creation timestamp |
| `updatedAt` | Instant | Last update timestamp |

### Entity: `Room`

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `hotelId` | UUID | Reference to the parent hotel |
| `name` | String | Room name or type label |
| `description` | String | Room description |
| `capacity` | Integer | Maximum number of guests |
| `pricePerNight` | BigDecimal | Price per night in EUR |
| `photos` | List<Photo> | List of room photos |
| `createdAt` | Instant | Creation timestamp |
| `updatedAt` | Instant | Last update timestamp |

### Entity: `Photo`

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `url` | String | Cloudinary public URL |
| `publicId` | String | Cloudinary public ID (used for deletion) |
| `uploadedAt` | Instant | Upload timestamp |

---

## Endpoints

### 1. `GET /api/hotels`

**Access:** Public

**Description:** Returns a paginated list of all hotels. Supports optional filtering by city and country.

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `city` | String | No | Filter by city |
| `country` | String | No | Filter by country |
| `page` | Integer | No | Page number (default: 0) |
| `size` | Integer | No | Page size (default: 10) |

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
      "country": "Spain",
      "stars": 5,
      "photos": [
        {
          "id": "f1e2d3c4-b5a6-7890-abcd-ef1234567890",
          "url": "https://res.cloudinary.com/demo/image/upload/hotel1.jpg",
          "uploadedAt": "2025-01-10T09:00:00Z"
        }
      ]
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

**Description:** Returns the full detail of a single hotel by its ID.

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Hotel identifier |

**Response `200 OK`:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Grand Hotel Barcelona",
  "description": "A luxury hotel in the heart of Barcelona.",
  "address": "Carrer de Pau Claris, 122",
  "city": "Barcelona",
  "country": "Spain",
  "stars": 5,
  "photos": [],
  "createdAt": "2025-01-10T09:00:00Z",
  "updatedAt": "2025-01-10T09:00:00Z"
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

**Description:** Creates a new hotel in the system.

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
  "country": "Spain",
  "stars": 5
}
```

**Validation Rules:**
- `name`, `address`, `city`, and `country` are required.
- `stars` must be between 1 and 5.

**Response `201 Created`:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Grand Hotel Barcelona",
  "city": "Barcelona",
  "country": "Spain",
  "stars": 5,
  "createdAt": "2025-01-15T10:30:00Z"
}
```

**Response `400 Bad Request`:**
```json
{
  "code": 400,
  "name": "BAD_REQUEST",
  "description": "Field 'name' is required",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 4. `PUT /api/hotels/{id}`

**Access:** ADMIN only

**Description:** Updates an existing hotel. All fields are optional; only the provided fields will be updated.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Hotel identifier |

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
  "stars": 4,
  "updatedAt": "2025-01-16T09:00:00Z"
}
```

**Response `404 Not Found`:**
```json
{
  "code": 404,
  "name": "NOT_FOUND",
  "description": "Hotel with id a1b2c3d4-e5f6-7890-abcd-ef1234567890 not found",
  "timestamp": "2025-01-16T09:00:00Z"
}
```

---

### 5. `DELETE /api/hotels/{id}`

**Access:** ADMIN only

**Description:** Deletes a hotel and all its associated rooms and photos. Photos are also deleted from Cloudinary.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Hotel identifier |

**Response `204 No Content`**

**Response `404 Not Found`:**
```json
{
  "code": 404,
  "name": "NOT_FOUND",
  "description": "Hotel with id a1b2c3d4-e5f6-7890-abcd-ef1234567890 not found",
  "timestamp": "2025-01-16T09:00:00Z"
}
```

---

### 6. `GET /api/hotels/{id}/rooms`

**Access:** Public

**Description:** Returns all rooms belonging to a specific hotel.

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Hotel identifier |

**Response `200 OK`:**
```json
[
  {
    "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
    "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "name": "Deluxe Double Room",
    "description": "Spacious room with sea views.",
    "capacity": 2,
    "pricePerNight": 180.00,
    "photos": []
  }
]
```

---

### 7. `GET /api/hotels/{id}/rooms/{roomId}`

**Access:** Public

**Description:** Returns the full detail of a specific room.

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Hotel identifier |
| `roomId` | UUID | Room identifier |

**Response `200 OK`:**
```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Deluxe Double Room",
  "description": "Spacious room with sea views.",
  "capacity": 2,
  "pricePerNight": 180.00,
  "photos": [],
  "createdAt": "2025-01-10T09:00:00Z",
  "updatedAt": "2025-01-10T09:00:00Z"
}
```

**Response `404 Not Found`:**
```json
{
  "code": 404,
  "name": "NOT_FOUND",
  "description": "Room with id b2c3d4e5-f6a7-8901-bcde-f12345678901 not found",
  "timestamp": "2025-01-15T10:30:00Z"
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
  "name": "Deluxe Double Room",
  "description": "Spacious room with sea views.",
  "capacity": 2,
  "pricePerNight": 180.00
}
```

**Validation Rules:**
- `name`, `capacity`, and `pricePerNight` are required.
- `capacity` must be a positive integer.
- `pricePerNight` must be a positive value.

**Response `201 Created`:**
```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "hotelId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Deluxe Double Room",
  "capacity": 2,
  "pricePerNight": 180.00,
  "createdAt": "2025-01-15T10:30:00Z"
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
  "pricePerNight": 200.00,
  "description": "Spacious room with sea views and a private balcony."
}
```

**Response `200 OK`:**
```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "pricePerNight": 200.00,
  "description": "Spacious room with sea views and a private balcony.",
  "updatedAt": "2025-01-16T09:00:00Z"
}
```

---

### 10. `DELETE /api/hotels/{id}/rooms/{roomId}`

**Access:** ADMIN only

**Description:** Deletes a room and all its associated photos. Photos are also removed from Cloudinary.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `204 No Content`**

---

### 11. `POST /api/hotels/{id}/photos`

**Access:** ADMIN only

**Description:** Uploads one or more photos for a hotel. Images are stored in Cloudinary and the returned URLs are saved in the database.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Request Body:** `multipart/form-data` with one or more image files under the key `files`.

**Response `201 Created`:**
```json
[
  {
    "id": "f1e2d3c4-b5a6-7890-abcd-ef1234567890",
    "url": "https://res.cloudinary.com/demo/image/upload/hotel1.jpg",
    "publicId": "hotels/hotel1",
    "uploadedAt": "2025-01-15T10:30:00Z"
  }
]
```

**Response `400 Bad Request`** — when no files are provided or the file type is not supported:
```json
{
  "code": 400,
  "name": "BAD_REQUEST",
  "description": "Only image files are accepted (jpg, png, webp)",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

### 12. `DELETE /api/hotels/{id}/photos/{photoId}`

**Access:** ADMIN only

**Description:** Deletes a specific photo from a hotel. The image is removed from both the database and Cloudinary.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `204 No Content`**

---

### 13. `POST /api/hotels/{id}/rooms/{roomId}/photos`

**Access:** ADMIN only

**Description:** Uploads one or more photos for a specific room. Images are stored in Cloudinary.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Request Body:** `multipart/form-data` with one or more image files under the key `files`.

**Response `201 Created`:**
```json
[
  {
    "id": "c4d5e6f7-a8b9-0123-cdef-456789012345",
    "url": "https://res.cloudinary.com/demo/image/upload/room1.jpg",
    "publicId": "rooms/room1",
    "uploadedAt": "2025-01-15T10:30:00Z"
  }
]
```

---

### 14. `DELETE /api/hotels/{id}/rooms/{roomId}/photos/{photoId}`

**Access:** ADMIN only

**Description:** Deletes a specific photo from a room. The image is removed from both the database and Cloudinary.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `204 No Content`**

---

## Error Format

All error responses follow a consistent structure:

| Field | Type | Description |
|---|---|---|
| `code` | Integer | HTTP status code |
| `name` | String | Short error identifier |
| `description` | String | Human-readable error message |
| `timestamp` | Instant | Time at which the error occurred |