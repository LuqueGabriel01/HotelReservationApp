package com.hotelreservation.auth.security.filters;

import com.hotelreservation.auth.constants.ApiPaths;
import com.hotelreservation.auth.constants.SecurityConstants;
import com.hotelreservation.auth.models.entities.Token;
import com.hotelreservation.auth.models.entities.User;
import com.hotelreservation.auth.repositories.TokenRepository;
import com.hotelreservation.auth.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** Custom security filter that intercept requests to validate JWT access tokens. */
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(SecurityConstants.Field.BEARER)) {
            filterChain.doFilter(request,response);
            return;
        }

        String jwtToken = authHeader.substring(7);
        String userEmail = jwtService.extractUser(jwtToken);

        if(userEmail == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request,response);
            return;
        }

        Token token = tokenRepository.findByToken(jwtToken)
                .orElse(null);
        if(token == null || token.isExpired() || token.isRevoked()){
            filterChain.doFilter(request,response);
            return;
        }

        User user = token.getUser();

        if (!jwtService.isValidToken(jwtToken, user)) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .authorities(user.getRole().name())
                .build();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals(ApiPaths.Auth.LOGIN)   ||
                path.equals(ApiPaths.Auth.REGISTER) ||
                path.equals(ApiPaths.Auth.REFRESH);
    }
}
