package com.hotelreservation.catalog.constants;

import lombok.NoArgsConstructor;

/** Centralized definition of API endpoint paths. */
@NoArgsConstructor
public final class ApiPaths {

    /** Hotel endpoints. */
    public static final class Hotel {
        public static final String BASE_URL = "/api/hotels";
        public static final String BY_ID = "/{id}";
        public static final String ROOMS = "/id}/rooms";
        public static final String ROOM_BY_ID = "/{id}/rooms/{id}";
        public static final String PHOTOS = "/{id}/photos";
        public static final String PHOTO_BY_ID = "/{id}/photos/{id}";
    }

}
