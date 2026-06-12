package com.hotelreservation.auth.models.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) representing a user authentication request.
 *
 * <p>This record is used to capture and validate the credentials provided by a user during the
 * login process.
 *
 * @param email The registered email address of the user.
 * @param password The user's account password.
 */
public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
