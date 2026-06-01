package com.hotelreservation.auth.models.dtos.request;

import com.hotelreservation.auth.validators.annotations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) representing a user authentication request.
 * <p>
 * This record is used to capture and validate the credentials provided by a user
 * during the login process.
 * </p>
 *
 * @param email
 * @param password
 * @author Gabriel Luque
 */
public record LoginRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @ValidPassword
        String password
) {
}
