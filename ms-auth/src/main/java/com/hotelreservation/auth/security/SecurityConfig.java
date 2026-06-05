package com.hotelreservation.auth.security;

import com.hotelreservation.auth.constants.ApiPaths;
import com.hotelreservation.auth.security.filters.AuthFilter;
import com.hotelreservation.auth.security.handlers.CustomAccessDenied;
import com.hotelreservation.auth.security.handlers.CustomAuthEntryPoint;
import com.hotelreservation.auth.security.handlers.CustomLogoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main security configuration class that establishes Spring Security rules for the application.
 * <p>
 * This class configures stateless session management, wires custom authentication mechanisms,
 * sets up endpoint authorization rules, and hooks up explicit handlers for authorization failures and sign-out logic.
 * </p>
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final AuthFilter authFilter;
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomAuthEntryPoint customAuthEntryPoint;
    private final CustomAccessDenied customAccessDenied;

    /**
     * Configures the {@link SecurityFilterChain} to define HTTP security policies,
     * public/private paths, custom filters, and error endpoints.
     *
     * @param http the HttpSecurity builder to scan and compile security rules
     * @return the constructed and active SecurityFilterChain
     * @throws Exception if a misconfiguration occurs during chain definition
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, ApiPaths.Actuator.HEALTH).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiPaths.Auth.LOGOUT).authenticated()
                        .requestMatchers(ApiPaths.Docs.SWAGGER_UI, ApiPaths.Docs.API_DOCS).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiPaths.Auth.REGISTER).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiPaths.Auth.REFRESH).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiPaths.Auth.LOGIN).permitAll()
                        .requestMatchers(HttpMethod.GET, ApiPaths.Auth.ME).authenticated()
                        .requestMatchers(HttpMethod.PUT, ApiPaths.Auth.ME).authenticated()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl(ApiPaths.Auth.LOGOUT)
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessHandler((request, response, authentication)
                                -> SecurityContextHolder.clearContext())
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthEntryPoint)
                        .accessDeniedHandler(customAccessDenied)
                )
                .build();
    }
}
