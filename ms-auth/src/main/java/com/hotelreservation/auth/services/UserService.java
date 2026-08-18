package com.hotelreservation.auth.services;

import com.hotelreservation.auth.models.dtos.request.LoginRequest;
import com.hotelreservation.auth.models.dtos.request.RegisterRequest;
import com.hotelreservation.auth.models.dtos.request.UpdateProfileRequest;
import com.hotelreservation.auth.models.dtos.response.RegisterResult;
import com.hotelreservation.auth.models.dtos.response.TokenResponse;
import com.hotelreservation.auth.models.dtos.response.UserResponse;
import java.util.UUID;

/** Service contract for managing user-related operations. */
public interface UserService {

  RegisterResult register(RegisterRequest request);

  TokenResponse login(LoginRequest request);

  TokenResponse refreshToken(String refreshToken);

  UserResponse getProfile(String email);

  RegisterResult updateProfile(UpdateProfileRequest request, String email);

  UserResponse getUserInfo(UUID id);
}
