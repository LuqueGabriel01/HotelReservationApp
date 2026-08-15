package com.hotelreservation.msagent.dto.recommend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Data transfer object (DTO) representing a room recommendation request for a guest's stay. */
public record RecommendRequest(
    @NotBlank String hotelId,
    @NotNull LocalDate checkIn,
    @NotNull LocalDate checkOut,
    @NotNull @Positive Integer guests,
    BigDecimal budget,
    String preferences) {}
