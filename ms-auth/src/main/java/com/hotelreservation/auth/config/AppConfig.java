package com.hotelreservation.auth.config;

import com.hotelreservation.auth.models.entities.User;
import com.hotelreservation.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Core application configuration class for setting up Spring Security infrastructure.
 *
 * <p>This class defines the necessary beans for user retrieval, password encoding, and
 * authentication management across the security filters.
 *
 * @author Gabriel Luque
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

  private final UserRepository repository;

  /**
   * Configures the {@link UserDetailsService} used by Spring Security to load user-specific data.
   */
  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      User user =
          repository
              .findByEmail(username)
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));
      return org.springframework.security.core.userdetails.User.builder()
          .username(user.getEmail())
          .password(user.getPassword())
          .disabled(!user.isEnabled())
          .authorities(user.getRole().name())
          .build();
    };
  }

  /**
   * Data access object (DAO) provider that leverages the custom {@link UserDetailsService} and
   * {@link PasswordEncoder} to authenticate credentials.
   *
   * @return the configured authentication provider for username and password verification
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Exposes the {@link AuthenticationManager} from the global authentication configuration.
   *
   * <p>The AuthenticationManager is responsible for processing authentication requests in the
   * application controllers.
   *
   * @param config the delegate configuration from Spring Security
   * @return the central authentication manager instance
   * @throws Exception if the authentication manager cannot be retrieved
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
  }

  /**
   * Defines the password encryption mechanism used across the application.
   *
   * @return a {@link BCryptPasswordEncoder} instance utilizing a secure hashing function
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
