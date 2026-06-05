package com.hotelreservation.auth.models.dtos.request;

import com.hotelreservation.auth.validators.annotations.ValidPassword;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;

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
