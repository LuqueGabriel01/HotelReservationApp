package com.hotelreservation.catalog.interceptors;

import com.hotelreservation.catalog.constants.HeaderConstants;
import com.hotelreservation.catalog.models.dto.response.ErrorResponse;
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

@Component
@RequiredArgsConstructor
public class RoleInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private static final List<String> ADMIN_ONLY_METHODS =
            List.of("POST", "PUT", "PATCH", "DELETE");

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception{

        String method = request.getMethod();

        if (ADMIN_ONLY_METHODS.contains(method)){
            String role = request.getHeader(HeaderConstants.Security.X_USER_ROLE);

            if (role == null) {
                writeError(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Missing role header");
                return false;
            }

            if (!role.equals("ADMIN")) {
                writeError(response, HttpStatus.FORBIDDEN, "FORBIDDEN", "Access denied");
                return false;
            }
        }
        return true;
    }

    private void writeError(HttpServletResponse response,
                            HttpStatus status,
                            String name,
                            String description) throws Exception{
        ErrorResponse errorResponse = new ErrorResponse(status.value(), name, description, Instant.now());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
