package com.hotelreservation.auth.services.impl;

import com.hotelreservation.auth.config.properties.JwtProperties;
import com.hotelreservation.auth.constants.ErrorConstants;
import com.hotelreservation.auth.enums.Role;
import com.hotelreservation.auth.enums.TokenType;
import com.hotelreservation.auth.exceptions.BadRequestException;
import com.hotelreservation.auth.exceptions.ConflictException;
import com.hotelreservation.auth.exceptions.UnauthorizedException;
import com.hotelreservation.auth.mappers.UserMapper;
import com.hotelreservation.auth.models.dtos.request.LoginRequest;
import com.hotelreservation.auth.models.dtos.request.RegisterRequest;
import com.hotelreservation.auth.models.dtos.request.UpdateProfileRequest;
import com.hotelreservation.auth.models.dtos.response.RegisterResponse;
import com.hotelreservation.auth.models.dtos.response.RegisterResult;
import com.hotelreservation.auth.models.dtos.response.TokenResponse;
import com.hotelreservation.auth.models.dtos.response.UserResponse;
import com.hotelreservation.auth.models.entities.Token;
import com.hotelreservation.auth.models.entities.User;
import com.hotelreservation.auth.repositories.TokenRepository;
import com.hotelreservation.auth.repositories.UserRepository;
import com.hotelreservation.auth.services.JwtService;
import com.hotelreservation.auth.services.UserService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing user identities, authentication flows, and profile
 * management.
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
   *
   * <p>Verifies that the requested email is unique, encodes the plaintext password, persists the
   * new user entity, and issues an initial pair of access and refresh tokens.
   *
   * @param request the registration payload containing user details and credentials
   * @return a {@link RegisterResult} pairing the client-facing response (no refresh token) with
   *     the raw refresh token, so the controller can set it as an {@code HttpOnly} cookie
   * @throws ConflictException if the provided email address is already registered in the system
   */
  @Override
  @Transactional
  public RegisterResult register(RegisterRequest request) {

    if (userRepository.existsByEmail(request.email())) {
      throw new ConflictException(ErrorConstants.Message.EMAIL_ALREADY_EXIST);
    }

    User user =
        User.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .email(request.email())
            .role(Role.ROLE_USER)
            .build();

    User savedUser = userRepository.save(user);

    String accessToken = jwtService.generateJwtToken(savedUser);
    String refreshToken = jwtService.generateRefreshToken(savedUser);

    TokenResponse tokens =
        new TokenResponse(
            accessToken,
            refreshToken,
            jwtProperties.getTokenPrefix(),
            jwtProperties.getExpiration().intValue());

    saveUserTokens(savedUser, accessToken, refreshToken);

    RegisterResponse response = userMapper.toRegisterResponse(savedUser, tokens);

    return new RegisterResult(response, tokens.refreshToken());
  }

  /**
   * Authenticates a user based on their email and password credentials.
   *
   * <p>Performs a record lookup and security validation. Upon successful authentication, triggers a
   * full token rotation sequence to invalidate previous sessions.
   *
   * @param request the login payload containing authentication credentials
   * @return a {@link TokenResponse} containing the new session tokens and configurations
   * @throws UnauthorizedException if no user account is found with the provided email
   * @throws BadRequestException if the password does not match the stored encoded password
   */
  @Override
  @Transactional
  public TokenResponse login(LoginRequest request) {

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new UnauthorizedException(ErrorConstants.Message.EMAIL_NOT_FOUND));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new BadRequestException(ErrorConstants.Message.INVALID_CREDENTIALS);
    }

    return rotateTokens(user);
  }

  /**
   * Generates a new pair of access and refresh tokens using a valid, non-expired refresh token.
   *
   * <p>The refresh token is read from the {@code HttpOnly} cookie by the controller and passed in
   * as a raw value (no {@code Bearer} prefix). Its database state and signature are validated
   * before a token rotation is performed.
   *
   * @param refreshToken the raw refresh token extracted from the cookie
   * @return a {@link TokenResponse} containing the renewed credentials
   * @throws UnauthorizedException if the token is missing, expired, revoked, or is not explicitly
   *     classified as a refresh token
   * @throws BadRequestException if the token subject does not map to any existing user record
   */
  @Override
  @Transactional
  public TokenResponse refreshToken(String refreshToken) {

    if (refreshToken == null || refreshToken.isBlank()) {
      throw new UnauthorizedException(ErrorConstants.Message.INVALID_REFRESH_TOKEN);
    }

    String userEmail = jwtService.extractUser(refreshToken);

    if (userEmail == null) {
      throw new UnauthorizedException(ErrorConstants.Message.INVALID_REFRESH_TOKEN);
    }

    User user =
        userRepository
            .findByEmail(userEmail)
            .orElseThrow(
                () -> new BadRequestException(ErrorConstants.Message.INVALID_REFRESH_TOKEN));

    Token storedToken =
        tokenRepository
            .findByToken(refreshToken)
            .orElseThrow(
                () -> new BadRequestException(ErrorConstants.Message.INVALID_REFRESH_TOKEN));

    if (storedToken.getTokenType() != TokenType.REFRESH
        || storedToken.isRevoked()
        || storedToken.isExpired()) {
      throw new UnauthorizedException(ErrorConstants.Message.INVALID_REFRESH_TOKEN);
    }

    return rotateTokens(user);
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
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new BadRequestException(ErrorConstants.Message.MISSING_INVALID_TOKEN));
    return userMapper.toUserResponse(user);
  }

  /**
   * Modifies the profile data of an authenticated user.
   *
   * <p>Processes partial or full updates dynamically for the username, email, and password fields.
   * If the email changes, uniqueness constraints are re-verified. A password modification triggers
   * re-encoding, and any modification enforces a token rotation for security integrity.
   *
   * @param request the payload containing the optional fields to update
   * @param email the current email address identifying the authenticated user
   * @return a {@link RegisterResult} pairing the client-facing response (no refresh token) with
   *     the raw refresh token, so the controller can set it as an {@code HttpOnly} cookie
   * @throws BadRequestException if the user record cannot be found
   * @throws ConflictException if the user attempts to update to an email already in use by another
   *     account
   */
  @Override
  @Transactional
  public RegisterResult updateProfile(UpdateProfileRequest request, String email) {

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new BadRequestException(ErrorConstants.Message.EMAIL_NOT_FOUND));

    if (request.username() != null) {
      user.changeUsername(request.username());
    }

    if (request.email() != null) {
      if (!user.getEmail().equals(request.email())
          && userRepository.existsByEmail(request.email())) {
        throw new ConflictException(ErrorConstants.Message.EMAIL_ALREADY_EXIST);
      }
      user.changeEmail(request.email());
    }

    if (request.password() != null
        && !passwordEncoder.matches(request.password(), user.getPassword())) {
      user.changePassword(passwordEncoder.encode(request.password()));
    }

    User savedUser = userRepository.save(user);

    TokenResponse tokens = rotateTokens(savedUser);

    RegisterResponse response = userMapper.toRegisterResponse(savedUser, tokens);

    return new RegisterResult(response, tokens.refreshToken());
  }

  /**
   * Retrieves the detailed information of a user by their unique identifier.
   *
   * <p>This method executes within a read-only transaction context to optimize database query
   * performance.
   *
   * @param id the unique identifier (UUID) of the user to look up
   * @return a {@link UserResponse} containing the mapped user data
   * @throws BadRequestException if no user is found with the provided ID
   */
  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserInfo(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestException(ErrorConstants.Message.INVALID_CREDENTIALS));
    return userMapper.toUserResponse(user);
  }

  private TokenResponse rotateTokens(User user) {
    String accessToken = jwtService.generateJwtToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserToken(user);
    saveUserTokens(user, accessToken, refreshToken);
    return new TokenResponse(
        accessToken,
        refreshToken,
        jwtProperties.getTokenPrefix(),
        jwtProperties.getExpiration().intValue());
  }

  private void revokeAllUserToken(User user) {
    tokenRepository.revokeAllActiveByUserId(user.getId(), Instant.now());
  }

  private void saveUserTokens(User user, String accessToken, String refreshToken) {
    tokenRepository.saveAll(
        List.of(
            buildToken(user, accessToken, TokenType.BEARER),
            buildToken(user, refreshToken, TokenType.REFRESH)));
  }

  private Token buildToken(User user, String jwtToken, TokenType type) {
    return Token.builder().user(user).token(jwtToken).tokenType(type).expired(false).build();
  }
}
