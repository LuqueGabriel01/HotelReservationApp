package com.hotelreservation.auth.mappers;

import com.hotelreservation.auth.models.dtos.response.RegisterResponse;
import com.hotelreservation.auth.models.dtos.response.TokenResponse;
import com.hotelreservation.auth.models.dtos.response.UserResponse;
import com.hotelreservation.auth.models.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Mapper interface to convert User entities into authentication DTO responses. */
@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "tokens", source = "tokens")
  RegisterResponse toRegisterResponse(User user, TokenResponse tokens);

  UserResponse toUserResponse(User user);
}
