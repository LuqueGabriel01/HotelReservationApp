package com.hotelreservation.auth.models.dtos.request;

import com.hotelreservation.auth.validators.annotations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) representing a user registration request.
 *
 * <p>This record captures the required credentials and profile information to create and persist a
 * new user account within the system.
 *
 * @param username username for user
 * @param password password, which must comply with {@link ValidPassword}
 * @param email valid email format
 * @author Gabriel Luque
 */
public record RegisterRequest(
    @NotBlank String username,
    @NotBlank @ValidPassword String password,
    @NotBlank @Email String email) {}
