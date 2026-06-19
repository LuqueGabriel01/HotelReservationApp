package com.hotelreservation.catalog.config;

import com.hotelreservation.catalog.constants.ApiPaths;
import com.hotelreservation.catalog.interceptors.RoleInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration class to register global interceptors and filters.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RoleInterceptor roleInterceptor;

    /**
     * Registers application interceptors to manage cross-cutting concerns like security or logging.
     * Maps the role validation interceptor specifically to the hotel API paths.
     *
     * @param registry Interceptor registry helper for the application context
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns(ApiPaths.Hotel.BASE_URL);
    }
}
