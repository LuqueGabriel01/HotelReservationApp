package com.hotelreservation.availability.interceptors;

import com.hotelreservation.availability.constants.HeaderConstants;
import com.hotelreservation.availability.models.dtos.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

/**
 * Security interceptor that restricts actions to unauthorized users based on HTTP headers.
 */
@Component
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private static final List<String> AUTHENTICATED_METHODS = List.of("POST", "DELETE");

    /**
     * Intercepts HTTP requests before they reach the controller. Checks if write operations are
     * authenticated by validating the presence of the required role header.
     *
     * @param request Current HTTP request metadata and headers
     * @param response Current HTTP response channel
     * @param handler Execution chain target handler
     * @return true if the user is authenticated to proceed, false otherwise
     * @throws Exception If an error occurs during JSON writing or response stream processing
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception{

        String method = request.getMethod();

        if (AUTHENTICATED_METHODS.contains(method)){
            String role = request.getHeader(HeaderConstants.Security.X_USER_ROLE);

            if (role == null){
                writeError(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Missing role header");
                return false;
            }
        }

        return true;
    }

    /**
     * Helper method to intercept and write a standardized JSON error payload to the response stream.
     *
     * @param response Current HTTP response channel
     * @param status Target HTTP status code
     * @param name Standard error categorization string
     * @param description Short human-readable explanation of the security violation
     * @throws Exception If serialization or writing to the response body fails
     */
    private void writeError(
            HttpServletResponse response, HttpStatus status, String name, String description)
            throws Exception {
        ErrorResponse errorResponse =
                new ErrorResponse(status.value(), name, description, Instant.now());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
