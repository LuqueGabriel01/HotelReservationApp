package com.hotelreservation.auth.controllers;

import com.hotelreservation.auth.constants.ApiPaths;
import com.hotelreservation.auth.constants.OpenApiConstants;
import com.hotelreservation.auth.constants.SecurityConstants;
import com.hotelreservation.auth.models.dtos.request.LoginRequest;
import com.hotelreservation.auth.models.dtos.request.RegisterRequest;
import com.hotelreservation.auth.models.dtos.request.UpdateProfileRequest;
import com.hotelreservation.auth.models.dtos.response.ErrorResponse;
import com.hotelreservation.auth.models.dtos.response.LoginResponse;
import com.hotelreservation.auth.models.dtos.response.RefreshResponse;
import com.hotelreservation.auth.models.dtos.response.RegisterResponse;
import com.hotelreservation.auth.models.dtos.response.RegisterResult;
import com.hotelreservation.auth.models.dtos.response.TokenResponse;
import com.hotelreservation.auth.models.dtos.response.UserResponse;
import com.hotelreservation.auth.security.cookie.RefreshTokenCookieFactory;
import com.hotelreservation.auth.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller for authentication requests. */
@RestController
@RequiredArgsConstructor
@Tag(
    name = "Authentication",
    description =
        "Endpoints for account registration, authentication, "
            + "token management, and user profile operations.")
public class AuthController {

  private final UserService userService;
  private final RefreshTokenCookieFactory tokenCookieFactory;

  /** register with cookie. */
  @Operation(
      summary = "Create a new account",
      description =
          """
                    Registers a new account in the platform using a unique username,
                    email address, and password. Once the registration process is
                    completed successfully, the service returns the newly created
                    user information together with an access token; the refresh token
                    is set as an HttpOnly cookie.
                    """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Information required to create a new account.",
      required = true,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = RegisterRequest.class)))
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Account created successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RegisterResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Invalid registration data.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.FORBIDDEN,
        description = OpenApiConstants.Message.FORBIDDEN_MESSAGE,
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.CONFLICT,
        description = "The username or email address is already in use.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(ApiPaths.Auth.REGISTER)
  public ResponseEntity<RegisterResponse> register(
      @RequestBody @Valid RegisterRequest request, HttpServletResponse response) {
    RegisterResult result = userService.register(request);
    response.addHeader(
        HttpHeaders.SET_COOKIE, tokenCookieFactory.create(result.refreshToken()).toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(result.response());
  }

  /** login with cookie. */
  @Operation(
      summary = "Sign in",
      description =
          """
                    Verifies the user's credentials and generates a new
                    authentication session. If the provided email and password
                    are valid, an access token and a refresh token are returned.
                    """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "User credentials.",
      required = true,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = LoginRequest.class)))
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Authentication completed successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = LoginResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Invalid request format.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.UNAUTHORIZED,
        description = "Email or password is incorrect.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.FORBIDDEN,
        description = OpenApiConstants.Message.FORBIDDEN_MESSAGE,
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(ApiPaths.Auth.LOGIN)
  public ResponseEntity<LoginResponse> login(
      @RequestBody @Valid LoginRequest request, HttpServletResponse response) {
    TokenResponse tokens = userService.login(request);
    response.addHeader(
        HttpHeaders.SET_COOKIE, tokenCookieFactory.create(tokens.refreshToken()).toString());
    LoginResponse loginCookie =
        LoginResponse.builder()
            .accessToken(tokens.accessToken())
            .tokenType(tokens.tokenType())
            .expiresIn(tokens.expiresIn())
            .build();
    return ResponseEntity.status(HttpStatus.OK).body(loginCookie);
  }

  /** refresh token with cookie. */
  @Operation(
      summary = "Refresh token endpoint",
      description =
          "Issues a new access token using a valid refresh token read from the HttpOnly "
              + "cookie, without requiring the user to log in again. The response renews the "
              + "cookie with the newly issued refresh token.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = OpenApiConstants.Code.SUCCESS,
            description = "Token refreshed successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = RefreshResponse.class))),
        @ApiResponse(
            responseCode = OpenApiConstants.Code.UNAUTHORIZED,
            description = "Missing, invalid, or expired refresh token cookie",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = OpenApiConstants.Code.FORBIDDEN,
            description = OpenApiConstants.Message.FORBIDDEN_MESSAGE,
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  @PostMapping(ApiPaths.Auth.REFRESH)
  public ResponseEntity<RefreshResponse> refreshToken(
      @CookieValue(value = SecurityConstants.Field.REFRESH_TOKEN_COOKIE, required = false)
          String refreshToken,
      HttpServletResponse response) {
    TokenResponse tokens = userService.refreshToken(refreshToken);
    response.addHeader(
        HttpHeaders.SET_COOKIE, tokenCookieFactory.create(tokens.refreshToken()).toString());

    RefreshResponse body =
        RefreshResponse.builder()
            .accessToken(tokens.accessToken())
            .tokenType(tokens.tokenType())
            .expiresIn(tokens.expiresIn())
            .build();
    return ResponseEntity.status(HttpStatus.OK).body(body);
  }

  /** profile. */
  @Operation(
      summary = "Get current user profile",
      description =
          "Retrieves the profile information of the currently "
              + "authenticated user based on the session or token.",
      security = @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = OpenApiConstants.Code.SUCCESS,
            description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(
            responseCode = OpenApiConstants.Code.UNAUTHORIZED,
            description = "Missing or invalid token",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = OpenApiConstants.Code.FORBIDDEN,
            description = OpenApiConstants.Message.FORBIDDEN_MESSAGE,
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  @GetMapping(ApiPaths.Auth.ME)
  public ResponseEntity<UserResponse> me(
      @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(userService.getProfile(userDetails.getUsername()));
  }

  /** profile with cookie. */
  @Operation(
      summary = "Update current user profile",
      description =
          "Updates the profile information of the currently authenticated user. Any "
              + "modification rotates the session tokens; the new refresh token is set as an "
              + "HttpOnly cookie.",
      security = @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH))
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Updated profile",
      required = true,
      content = @Content(schema = @Schema(implementation = UpdateProfileRequest.class)))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = OpenApiConstants.Code.SUCCESS,
            description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
        @ApiResponse(
            responseCode = OpenApiConstants.Code.BAD_REQUEST,
            description = "invalid input data",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = OpenApiConstants.Code.UNAUTHORIZED,
            description = "Unauthorized - Missing or invalid token",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = OpenApiConstants.Code.FORBIDDEN,
            description = OpenApiConstants.Message.FORBIDDEN_MESSAGE,
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = OpenApiConstants.Code.CONFLICT,
            description = "Conflict - invalid email or email already exists",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  @PutMapping(ApiPaths.Auth.ME)
  public ResponseEntity<RegisterResponse> me(
      @RequestBody @Valid UpdateProfileRequest request,
      @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
      HttpServletResponse response) {
    RegisterResult result = userService.updateProfile(request, userDetails.getUsername());
    response.addHeader(
        HttpHeaders.SET_COOKIE, tokenCookieFactory.create(result.refreshToken()).toString());
    return ResponseEntity.status(HttpStatus.OK).body(result.response());
  }

  @Operation(
      summary = "Logout",
      description = "Revokes the current user's tokens.",
      security = @SecurityRequirement(name = "BearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Logout completed successfully."),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.UNAUTHORIZED,
        description = "Missing or invalid authorization token.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.FORBIDDEN,
        description = OpenApiConstants.Message.FORBIDDEN_MESSAGE,
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(ApiPaths.Auth.LOGOUT)
  public void logout() {
    // Handled by Spring Security - CustomLogoutHandler
  }

  @Operation(
      summary = "Get user details for internal services",
      description = "Retrieves user details by their ID. Intended for inter-service communication.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = OpenApiConstants.Code.SUCCESS,
            description = "Profile updated successfully",
            content = @Content(schema = @Schema(implementation = RegisterResponse.class)))
      })
  @GetMapping(ApiPaths.Auth.INTERNAL_ME)
  public ResponseEntity<UserResponse> internalMe(@PathVariable UUID userId) {
    return ResponseEntity.ok(userService.getUserInfo(userId));
  }
}
