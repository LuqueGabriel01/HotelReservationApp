package com.hotelreservation.auth.security.handlers;

import com.hotelreservation.auth.constants.ErrorConstants;
import com.hotelreservation.auth.security.writer.ErrorWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Custom handler to manage authenticated requests that fail authorization checks. */
@Component
@RequiredArgsConstructor
public class CustomAccessDenied implements AccessDeniedHandler {

    private final ErrorWriter writer;

    /** Intercepts authorization failures and writes a standardized error response. */
    @Override
    public void handle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull AccessDeniedException accessDeniedException) throws IOException {
        writer.write(response, HttpStatus.UNAUTHORIZED, ErrorConstants.Message.ACCESS_DENIED);
    }
}
