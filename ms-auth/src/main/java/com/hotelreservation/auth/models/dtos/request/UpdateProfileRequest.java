package com.hotelreservation.auth.models.dtos.request;

import com.hotelreservation.auth.validators.annotations.ValidPassword;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;

/** DTO record representing the optional data required to update a user profile. */
public record UpdateProfileRequest(
        @Nullable
        String username,

        @Nullable
        @ValidPassword
        String password,

        @Nullable
        @Email
        String email
) {}
