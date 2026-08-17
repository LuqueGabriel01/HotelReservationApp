package com.hotelreservation.availability.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/** Configuration class for OpenAPI (Swagger) documentation. */
@OpenAPIDefinition(
    info =
        @Info(
            title = "Catalog microservice API",
            description = "REST API for catalog management",
            version = "1.0"))
public class OpenApiConfig {}
