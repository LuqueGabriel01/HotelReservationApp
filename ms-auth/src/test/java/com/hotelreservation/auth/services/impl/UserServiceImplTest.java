package com.hotelreservation.auth.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hotelreservation.auth.config.properties.JwtProperties;
import com.hotelreservation.auth.constants.ErrorConstants;
import com.hotelreservation.auth.exceptions.BadRequestException;
import com.hotelreservation.auth.exceptions.ConflictException;
import com.hotelreservation.auth.exceptions.UnauthorizedException;
import com.hotelreservation.auth.fixtures.TokenFixture;
import com.hotelreservation.auth.fixtures.UserFixture;
import com.hotelreservation.auth.mappers.UserMapper;
import com.hotelreservation.auth.models.dtos.request.LoginRequest;
import com.hotelreservation.auth.models.dtos.request.RegisterRequest;
import com.hotelreservation.auth.models.dtos.request.UpdateProfileRequest;
import com.hotelreservation.auth.models.dtos.response.AccessTokenResponse;
import com.hotelreservation.auth.models.dtos.response.RegisterResponse;
import com.hotelreservation.auth.models.dtos.response.RegisterResult;
import com.hotelreservation.auth.models.dtos.response.TokenResponse;
import com.hotelreservation.auth.models.entities.Token;
import com.hotelreservation.auth.models.entities.User;
import com.hotelreservation.auth.repositories.TokenRepository;
import com.hotelreservation.auth.repositories.UserRepository;
import com.hotelreservation.auth.services.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private TokenRepository tokenRepository;
  @Mock private UserMapper userMapper;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private JwtProperties jwtProperties;

  @InjectMocks private UserServiceImpl userServiceImpl;

  @Nested
  @DisplayName("test for register()")
  class Register {

    @Test
    @DisplayName("should throw ConflictException when email already exists")
    void shouldThrowConflictExceptionWhenEmailExists() {

      User user = UserFixture.buildDefaultUser();

      RegisterRequest request =
          new RegisterRequest(user.getUsername(), user.getPassword(), user.getEmail());

      Mockito.when(userRepository.existsByEmail(request.email())).thenReturn(true);

      ConflictException exception =
          assertThrows(ConflictException.class, () -> userServiceImpl.register(request));
      assertThat(exception.getMessage()).isEqualTo(ErrorConstants.Message.EMAIL_ALREADY_EXIST);

      Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("should register user successfully when email is unique")
    void shouldReturnRegisterResponseWhenUserSuccessfully() {
      User user = UserFixture.buildDefaultUser();

      String dummyAccess = "access_token_mock";
      String dummyRefresh = "refresh_token_mock";

      AccessTokenResponse expectedTokens =
          AccessTokenResponse.builder()
              .accessToken(dummyAccess)
              .tokenType("Bearer")
              .expiresIn(3600)
              .build();

      RegisterResponse response =
          RegisterResponse.builder()
              .id(user.getId())
              .username(user.getUsername())
              .email(user.getEmail())
              .role(user.getRole())
              .tokens(expectedTokens)
              .build();

      RegisterRequest request =
          new RegisterRequest(user.getUsername(), user.getPassword(), user.getEmail());

      Mockito.when(userRepository.existsByEmail(request.email())).thenReturn(false);
      Mockito.when(passwordEncoder.encode(request.password())).thenReturn("encrypted_password_123");
      Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
      Mockito.when(jwtService.generateJwtToken(user)).thenReturn("access_token_mock");
      Mockito.when(jwtService.generateRefreshToken(user)).thenReturn("refresh_token_mock");
      Mockito.when(jwtProperties.getExpiration()).thenReturn(3600L);
      Mockito.when(
              userMapper.toRegisterResponse(Mockito.eq(user), Mockito.any(TokenResponse.class)))
          .thenReturn(response);

      RegisterResult result = userServiceImpl.register(request);
      RegisterResponse actualResponse = result.response();

      assertThat(actualResponse).isNotNull();
      assertThat(actualResponse.email()).isEqualTo(request.email());
      assertThat(actualResponse.tokens().accessToken()).isEqualTo(dummyAccess);
      assertThat(result.refreshToken()).isEqualTo(dummyRefresh);

      Mockito.verify(passwordEncoder, Mockito.times(1)).encode(request.password());
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
      Mockito.verify(tokenRepository, Mockito.times(1)).saveAll(Mockito.anyList());
    }
  }

  @Nested
  @DisplayName("Test for Login()")
  class Login {

    @Test
    @DisplayName("should throw UnauthorizedException when email does not exist")
    void shouldReturnUnauthorizedException() {

      User user = UserFixture.buildDefaultUser();

      LoginRequest request = new LoginRequest(user.getUsername(), user.getPassword());

      Mockito.when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

      assertThrows(UnauthorizedException.class, () -> userServiceImpl.login(request));

      Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("should throw BadRequestException when password does not match")
    void shouldReturnBadRequestException() {
      User user = UserFixture.buildDefaultUser();
      LoginRequest request = new LoginRequest(user.getUsername(), user.getPassword());

      Mockito.when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
      Mockito.when(passwordEncoder.matches(request.password(), user.getPassword()))
          .thenReturn(false);

      assertThrows(BadRequestException.class, () -> userServiceImpl.login(request));

      Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("should return TokenResponse successfully when credentials are valid")
    void shouldReturnUserWhenIsValid() {

      String dummyAccess = "access_token_mock";
      String dummyRefresh = "refresh_token_mock";

      User user = UserFixture.buildDefaultUser();
      LoginRequest request = new LoginRequest(user.getEmail(), user.getPassword());

      Mockito.when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
      Mockito.when(passwordEncoder.matches(request.password(), user.getPassword()))
          .thenReturn(true);

      Mockito.when(jwtService.generateJwtToken(user)).thenReturn(dummyAccess);
      Mockito.when(jwtService.generateRefreshToken(user)).thenReturn(dummyRefresh);

      Mockito.when(jwtProperties.getExpiration()).thenReturn(3600L);

      TokenResponse response = userServiceImpl.login(request);

      assertThat(response).isNotNull();
      assertThat(response.accessToken()).isEqualTo(dummyAccess);
      assertThat(response.refreshToken()).isEqualTo(dummyRefresh);
      assertThat(response.expiresIn()).isEqualTo(3600);

      Mockito.verify(tokenRepository, Mockito.times(1))
          .revokeAllActiveByUserId(Mockito.eq(user.getId()), Mockito.any());
      Mockito.verify(tokenRepository, Mockito.times(1)).saveAll(Mockito.anyList());
    }
  }

  @Nested
  @DisplayName("Test for refreshToken()")
  class RefreshToken {

    @Test
    @DisplayName("should throw UnauthorizedException when token is null")
    void shouldThrowUnauthorizedExceptionWhenTokenIsNull() {
      assertThrows(UnauthorizedException.class, () -> userServiceImpl.refreshToken(null));
    }

    @Test
    @DisplayName("should throw UnauthorizedException when token is blank")
    void shouldThrowUnauthorizedExceptionWhenTokenIsBlank() {
      assertThrows(UnauthorizedException.class, () -> userServiceImpl.refreshToken("   "));
    }

    @Test
    @DisplayName("should throw BadRequestException when token does not exist in DB")
    void shouldThrowBadRequestExceptionWhenTokenNotFound() {
      User user = UserFixture.buildDefaultUser();

      Mockito.when(jwtService.extractUser("refresh.token")).thenReturn(user.getEmail());
      Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
      Mockito.when(tokenRepository.findByToken("refresh.token")).thenReturn(Optional.empty());

      assertThrows(BadRequestException.class, () -> userServiceImpl.refreshToken("refresh.token"));
    }

    @Test
    @DisplayName("should throw UnauthorizedException when token type is not REFRESH")
    void shouldThrowUnauthorizedExceptionWhenTokenTypeIsNotRefresh() {
      User user = UserFixture.buildDefaultUser();

      Token bearerToken = TokenFixture.buildActiveAccessToken(user); // TokenType.BEARER

      Mockito.when(jwtService.extractUser("refresh.token")).thenReturn(user.getEmail());
      Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
      Mockito.when(tokenRepository.findByToken("refresh.token"))
          .thenReturn(Optional.of(bearerToken));

      assertThrows(
          UnauthorizedException.class, () -> userServiceImpl.refreshToken("refresh.token"));
    }

    @Test
    @DisplayName("should throw UnauthorizedException when token is revoked")
    void shouldThrowUnauthorizedExceptionWhenTokenIsRevoked() {
      User user = UserFixture.buildDefaultUser();

      Token revokedToken = TokenFixture.buildInactive(user); // revoked=true, expired=true

      Mockito.when(jwtService.extractUser("refresh.token")).thenReturn(user.getEmail());
      Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
      Mockito.when(tokenRepository.findByToken("refresh.token"))
          .thenReturn(Optional.of(revokedToken));

      assertThrows(
          UnauthorizedException.class, () -> userServiceImpl.refreshToken("refresh.token"));
    }

    @Test
    @DisplayName("should return TokenResponse when token is valid")
    void shouldReturnTokenResponseWhenTokenIsValid() {
      String dummyAccess = "new_access_token";
      String dummyRefresh = "new_refresh_token";

      User user = UserFixture.buildDefaultUser();
      Token validRefreshToken = TokenFixture.buildActiveRefresh(user);

      Mockito.when(jwtService.extractUser(TokenFixture.DEFAULT_REFRESH_TOKEN))
          .thenReturn(user.getEmail());
      Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
      Mockito.when(tokenRepository.findByToken(TokenFixture.DEFAULT_REFRESH_TOKEN))
          .thenReturn(Optional.of(validRefreshToken));
      Mockito.when(jwtService.generateJwtToken(user)).thenReturn(dummyAccess);
      Mockito.when(jwtService.generateRefreshToken(user)).thenReturn(dummyRefresh);
      Mockito.when(jwtProperties.getExpiration()).thenReturn(3600L);

      TokenResponse response = userServiceImpl.refreshToken(TokenFixture.DEFAULT_REFRESH_TOKEN);

      assertThat(response).isNotNull();
      assertThat(response.accessToken()).isEqualTo(dummyAccess);
      assertThat(response.refreshToken()).isEqualTo(dummyRefresh);
      assertThat(response.expiresIn()).isEqualTo(3600);

      Mockito.verify(tokenRepository)
          .revokeAllActiveByUserId(Mockito.eq(user.getId()), Mockito.any());
      Mockito.verify(tokenRepository).saveAll(Mockito.anyList());
    }
  }

  @Nested
  @DisplayName("Test for updateProfile()")
  class UpdateProfile {

    @Test
    @DisplayName("should throw ConflictException when new email is already in use")
    void shouldThrowConflictExceptionWhenEmailAlreadyInUse() {
      User user = UserFixture.buildDefaultUser();
      UpdateProfileRequest request = new UpdateProfileRequest(null, null, "taken@test.com");

      Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
      Mockito.when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

      assertThrows(
          ConflictException.class, () -> userServiceImpl.updateProfile(request, user.getEmail()));

      Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("should update only username when only username is provided")
    void shouldUpdateOnlyUsernameWhenOnlyUsernameIsProvided() {
      User user = UserFixture.buildDefaultUser();
      UpdateProfileRequest request = new UpdateProfileRequest("new_username", null, null);

      String dummyAccess = "access_token_mock";
      String dummyRefresh = "refresh_token_mock";
      AccessTokenResponse expectedTokens =
          AccessTokenResponse.builder()
              .accessToken(dummyAccess)
              .tokenType("Bearer")
              .expiresIn(3600)
              .build();

      RegisterResponse expectedResponse =
          RegisterResponse.builder()
              .id(user.getId())
              .username("new_username")
              .email(user.getEmail())
              .role(user.getRole())
              .tokens(expectedTokens)
              .build();

      Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
      Mockito.when(userRepository.save(user)).thenReturn(user);
      Mockito.when(jwtService.generateJwtToken(user)).thenReturn(dummyAccess);
      Mockito.when(jwtService.generateRefreshToken(user)).thenReturn(dummyRefresh);
      Mockito.when(jwtProperties.getExpiration()).thenReturn(3600L);
      Mockito.when(
              userMapper.toRegisterResponse(Mockito.eq(user), Mockito.any(TokenResponse.class)))
          .thenReturn(expectedResponse);

      RegisterResult result = userServiceImpl.updateProfile(request, user.getEmail());
      RegisterResponse response = result.response();

      assertThat(response.username()).isEqualTo("new_username");
      assertThat(response.email()).isEqualTo(user.getEmail());
      assertThat(result.refreshToken()).isEqualTo(dummyRefresh);

      Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.any());
    }

    @Test
    @DisplayName("should re-encode password and rotate tokens when password is updated")
    void shouldReEncodePasswordAndRotateTokensWhenPasswordIsUpdated() {
      User user = UserFixture.buildDefaultUser();
      UpdateProfileRequest request = new UpdateProfileRequest(null, "NewPassword@123", null);

      String dummyAccess = "access_token_mock";
      String dummyRefresh = "refresh_token_mock";
      AccessTokenResponse expectedTokens =
          AccessTokenResponse.builder()
              .accessToken(dummyAccess)
              .tokenType("Bearer")
              .expiresIn(3600)
              .build();

      RegisterResponse expectedResponse =
          RegisterResponse.builder()
              .id(user.getId())
              .username(user.getUsername())
              .email(user.getEmail())
              .role(user.getRole())
              .tokens(expectedTokens)
              .build();

      Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
      Mockito.when(passwordEncoder.matches("NewPassword@123", user.getPassword()))
          .thenReturn(false);
      Mockito.when(passwordEncoder.encode("NewPassword@123")).thenReturn("new_encoded_password");
      Mockito.when(userRepository.save(user)).thenReturn(user);
      Mockito.when(jwtService.generateJwtToken(user)).thenReturn(dummyAccess);
      Mockito.when(jwtService.generateRefreshToken(user)).thenReturn(dummyRefresh);
      Mockito.when(jwtProperties.getExpiration()).thenReturn(3600L);
      Mockito.when(
              userMapper.toRegisterResponse(Mockito.eq(user), Mockito.any(TokenResponse.class)))
          .thenReturn(expectedResponse);

      RegisterResult result = userServiceImpl.updateProfile(request, user.getEmail());

      assertThat(result.response()).isNotNull();
      assertThat(result.refreshToken()).isEqualTo(dummyRefresh);

      Mockito.verify(passwordEncoder).encode("NewPassword@123");
      Mockito.verify(tokenRepository)
          .revokeAllActiveByUserId(Mockito.eq(user.getId()), Mockito.any());
      Mockito.verify(tokenRepository).saveAll(Mockito.anyList());
    }
  }
}
