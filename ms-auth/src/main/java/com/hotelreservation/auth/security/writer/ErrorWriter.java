package com.hotelreservation.auth.security.writer;

import com.hotelreservation.auth.models.dtos.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

/**
 * Utility component used to write standardized JSON error responses directly to the HTTP output stream.
 * <p>
 * This helper is mainly utilized by security handlers and filters to bypass the standard Spring MVC
 * ExceptionHandler infrastructure when dispatching auth-related errors.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ErrorWriter {

    private final JsonMapper objectMapper;

    /**
     * Writes an error response to the HTTP response stream.
     *
     * @param response the HttpServletResponse to write to
     * @param status the HTTP status to set
     * @param description a description of the error
     * @throws IOException if writing to the response fails
     */
    public void write(HttpServletResponse response, HttpStatus status, String description)
            throws IOException {

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .code(status.value())
                        .name(status.getReasonPhrase())
                        .description(description)
                        .build();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
