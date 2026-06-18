package com.hotelreservation.catalog.config;

import com.hotelreservation.catalog.constants.ApiPaths;
import com.hotelreservation.catalog.interceptors.RoleInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns(ApiPaths.Hotel.BASE_URL);
    }
}
