package com.hotelreservation.auth.config;

import com.hotelreservation.auth.constants.OpenApiConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/** Configuration class for OpenAPI (Swagger) documentation. */
@OpenAPIDefinition(
    info = @Info(title = "Authentication Api", description = "REST API", version = "1.0"))
@SecurityScheme(
    name = OpenApiConstants.BEARER_AUTH,
    description = "JWT Authentication header",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
public class OpenApiConfig {}
