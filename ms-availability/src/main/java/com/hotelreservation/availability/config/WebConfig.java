package com.hotelreservation.availability.config;

import com.hotelreservation.availability.constants.ApiPaths;
import com.hotelreservation.availability.interceptors.UserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Web MVC configuration to register global interceptors. */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final UserInterceptor userInterceptor;

  /**
   * Registers the user validation interceptor for the availability API paths.
   *
   * @param registry The interceptor registry.
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(userInterceptor).addPathPatterns(ApiPaths.BASE_URL);
  }
}
