package com.hotelreservation.msgateway.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.Builder;

/** data transfer object (DTO) representing an API error response. */
@Builder
public record ErrorResponse(Integer code, String name, String description, Instant timestamp) {

  /**
   * Factory method to create an {@code ErrorResponse} instance with the current timestamp.
   * Automatically truncates the generated timestamp to milliseconds for consistency. * @param code
   * the HTTP status integer code.
   *
   * @param name the name or short reason phrase of the HTTP status.
   * @param description a detailed error message.
   * @return a fully populated {@link ErrorResponse} instance.
   */
  public static ErrorResponse of(Integer code, String name, String description) {
    return ErrorResponse.builder()
        .code(code)
        .name(name)
        .description(description)
        .timestamp(Instant.now().truncatedTo(ChronoUnit.MILLIS))
        .build();
  }
}
