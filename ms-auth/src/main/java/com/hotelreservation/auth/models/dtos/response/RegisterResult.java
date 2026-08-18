package com.hotelreservation.auth.models.dtos.response;

/**
 * Internal carrier pairing a client-facing {@link RegisterResponse} with the raw refresh token.
 *
 * <p>Not returned directly to clients. {@code UserServiceImpl} produces it so the controller can
 * set the refresh token as an {@code HttpOnly} cookie while sending {@link #response()} — which
 * never contains the refresh token — as the JSON body.
 *
 * @param response the client-facing response body, already stripped of the refresh token
 * @param refreshToken the raw refresh token to attach as a {@code Set-Cookie} header
 */
public record RegisterResult(RegisterResponse response, String refreshToken) {}
