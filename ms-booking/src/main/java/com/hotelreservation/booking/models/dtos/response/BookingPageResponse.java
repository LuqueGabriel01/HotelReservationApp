package com.hotelreservation.booking.models.dtos.response;

import java.util.List;

/** Generic paginated response wrapper for booking list endpoints. */
public record BookingPageResponse<T>(
    List<T> content, Integer page, Integer size, Long totalElements, Integer totalPages) {}
