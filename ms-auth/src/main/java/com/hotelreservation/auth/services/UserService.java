package com.hotelreservation.auth.services;

import com.hotelreservation.auth.models.dtos.request.LoginRequest;
import com.hotelreservation.auth.models.dtos.request.RegisterRequest;
import com.hotelreservation.auth.models.dtos.request.UpdateProfileRequest;
import com.hotelreservation.auth.models.dtos.response.LoginResponse;
import com.hotelreservation.auth.models.dtos.response.RefreshResponse;
import com.hotelreservation.auth.models.dtos.response.RegisterResponse;
import com.hotelreservation.auth.models.dtos.response.UserResponse;

/** Service contract for managing user-related operations. */
public interface UserService {

  RegisterResponse register(RegisterRequest request);

  LoginResponse login(LoginRequest request);

  RefreshResponse refreshToken(String authHeader);

  UserResponse getProfile(String email);

  RegisterResponse updateProfile(UpdateProfileRequest request, String email);
}
