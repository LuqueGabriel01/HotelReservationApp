package com.hotelreservation.catalog.models.dto.response;

import java.util.List;

/**
 * Generic data transfer object container used to structure paginated query results across the
 * system.
 */
public record PageResponseDto<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {}
