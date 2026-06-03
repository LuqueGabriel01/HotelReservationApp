package com.hotelreservation.auth.config;

import com.hotelreservation.auth.config.properties.JwtProperties;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

/**
 * Configuration class responsible for establishing cryptographic keys used in JWT operations.
 * <p>
 * This class processes the raw secret defined in the application properties to generate
 * a secure, typed cryptographic key required for signing and verifying JSON Web Tokens.
 * </p>
 *
 * @author Gabriel Luque
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtProperties jwt;

    /**
     * Creates and configures a {@link SecretKey} bean for HMAC-SHA signing operations.
     * <p>
     * The method decodes the Base64-encoded secret string retrieved from {@link JwtProperties}
     * and transforms it into a robust cryptographic key.
     * </p>
     *
     * @throws IllegalArgumentException if the secret key string is improperly encoded or too short
     */
    @Bean
    public SecretKey signingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwt.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
