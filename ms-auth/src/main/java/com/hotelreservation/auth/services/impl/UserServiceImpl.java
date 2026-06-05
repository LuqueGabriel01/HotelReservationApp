package com.hotelreservation.auth.services.impl;

import com.hotelreservation.auth.config.properties.JwtProperties;
import com.hotelreservation.auth.constants.ErrorConstants;
import com.hotelreservation.auth.constants.SecurityConstants;
import com.hotelreservation.auth.enums.Role;
import com.hotelreservation.auth.enums.TokenType;
import com.hotelreservation.auth.exceptions.BadRequestException;
import com.hotelreservation.auth.exceptions.ConflictException;
import com.hotelreservation.auth.exceptions.UnauthorizedException;
import com.hotelreservation.auth.mappers.UserMapper;
import com.hotelreservation.auth.models.dtos.request.LoginRequest;
import com.hotelreservation.auth.models.dtos.request.RegisterRequest;
import com.hotelreservation.auth.models.dtos.request.UpdateProfileRequest;
import com.hotelreservation.auth.models.dtos.response.*;
import com.hotelreservation.auth.models.entities.Token;
import com.hotelreservation.auth.models.entities.User;
import com.hotelreservation.auth.repositories.TokenRepository;
import com.hotelreservation.auth.repositories.UserRepository;
import com.hotelreservation.auth.services.JwtService;
import com.hotelreservation.auth.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Service implementation for managing user identities, authentication flows, and profile management.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    /**
     * Registers a new user in the system with default member privileges.
     * <p>
     * Verifies that the requested email is unique, encodes the plaintext password, persists the
     * new user entity, and issues an initial pair of access and refresh tokens.
     * </p>
     *
     * @param request the registration payload containing user details and credentials
     * @return a {@link RegisterResponse} containing the created profile data and authentication tokens
     * @throws ConflictException if the provided email address is already registered in the system
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.email())){
            throw new ConflictException(ErrorConstants.Message.EMAIL_ALREADY_EXIST);
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateJwtToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        TokenResponse tokens = new TokenResponse(accessToken, refreshToken);

        saveUserTokens(savedUser, accessToken, refreshToken);

        return userMapper.toRegisterResponse(savedUser, tokens);
    }

    /**
     * Authenticates a user based on their email and password credentials.
     * <p>
     * Performs a record lookup and security validation. Upon successful authentication,
     * triggers a full token rotation sequence to invalidate previous sessions.
     * </p>
     *
     * @param request the login payload containing authentication credentials
     * @return a {@link LoginResponse} containing the new session tokens and configurations
     * @throws UnauthorizedException if no user account is found with the provided email
     * @throws BadRequestException   if the password does not match the stored encoded password
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException(ErrorConstants.Message.EMAIL_NOT_FOUND));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new BadRequestException(ErrorConstants.Message.INVALID_CREDENTIALS);
        }

        TokenResponse tokens = rotateTokens(user);

        return LoginResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .expiresIn(jwtProperties.getExpiration().intValue())
                .tokenType(jwtProperties.getTokenPrefix())
                .build();
    }

    /**
     * Generates a new pair of access and refresh tokens using a valid, non-expired refresh token.
     * <p>
     * Extracts the token from the Authorization header, validates its database state and signature,
     * and performs a token rotation if all checks pass.
     * </p>
     *
     * @param authHeader the Authorization HTTP header containing the Bearer refresh token
     * @return a {@link RefreshResponse} containing the renewed credentials
     * @throws UnauthorizedException if the header is missing/malformed, the token is expired/revoked,
     * or is not explicitly classified as a refresh token
     * @throws BadRequestException   if the token subject does not map to any existing user record
     */
    @Override
    @Transactional
    public RefreshResponse refreshToken(String authHeader) {

        if(authHeader == null || !authHeader.startsWith(SecurityConstants.Field.BEARER)){
            throw new UnauthorizedException(ErrorConstants.Message.INVALID_REFRESH_TOKEN);
        }

        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUser(refreshToken);

        if(userEmail == null){
            throw new UnauthorizedException(ErrorConstants.Message.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException(ErrorConstants.Message.INVALID_REFRESH_TOKEN));

        Token storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadRequestException(ErrorConstants.Message.INVALID_REFRESH_TOKEN));

        if(storedToken.getTokenType() != TokenType.REFRESH || storedToken.isRevoked() || storedToken.isExpired()){
            throw new UnauthorizedException(ErrorConstants.Message.INVALID_REFRESH_TOKEN);
        }

        TokenResponse tokens = rotateTokens(user);

        return new RefreshResponse(tokens.accessToken(), tokens.refreshToken(), jwtProperties.getTokenPrefix(),jwtProperties.getExpiration().intValue());
    }

    /**
     * Retrieves the profile information of a user by their unique email address.
     *
     * @param email the email address of the target user
     * @return a {@link UserResponse} wrapping the user's information and metadata
     * @throws BadRequestException if no user account matches the provided email
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorConstants.Message.MISSING_INVALID_TOKEN));
        return userMapper.toUserResponse(user);
    }

    /**
     * Modifies the profile data of an authenticated user.
     * <p>
     * Processes partial or full updates dynamically for the username, email, and password fields.
     * If the email changes, uniqueness constraints are re-verified. A password modification triggers
     * re-encoding, and any modification enforces a token rotation for security integrity.
     * </p>
     *
     * @param request the payload containing the optional fields to update
     * @param email   the current email address identifying the authenticated user
     * @return a {@link RegisterResponse} containing the updated profile and fresh security tokens
     * @throws BadRequestException if the user record cannot be found
     * @throws ConflictException   if the user attempts to update to an email already in use by another account
     */
    @Override
    @Transactional
    public RegisterResponse updateProfile(UpdateProfileRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorConstants.Message.EMAIL_NOT_FOUND));

        if(request.username() != null){
            user.changeUsername(request.username());
        }

        if (request.email() != null) {
            if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
                throw new ConflictException(ErrorConstants.Message.EMAIL_ALREADY_EXIST);
            }
            user.changeEmail(request.email());
        }

        if(request.password() != null && !passwordEncoder.matches(request.password(), user.getPassword())) {
            user.changePassword(passwordEncoder.encode(request.password()));
        }

        User savedUser = userRepository.save(user);

        TokenResponse tokens = rotateTokens(savedUser);

        return userMapper.toRegisterResponse(savedUser, tokens);
    }

    private TokenResponse rotateTokens(User user) {
        String accessToken = jwtService.generateJwtToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserToken(user);
        saveUserTokens(user, accessToken, refreshToken);
        return new TokenResponse(accessToken, refreshToken);
    }

    private void revokeAllUserToken(User user) {
        tokenRepository.revokeAllActiveByUserId(user.getId(), Instant.now());
    }

    private void saveUserTokens(User user, String accessToken, String refreshToken) {
        tokenRepository.saveAll(List.of(
                buildToken(user, accessToken, TokenType.BEARER),
                buildToken(user, refreshToken, TokenType.REFRESH)
        ));
    }

    private Token buildToken(User user, String jwtToken, TokenType type) {
        return Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(type)
                .expired(false)
                .build();
    }
}