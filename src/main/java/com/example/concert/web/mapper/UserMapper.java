package com.example.concert.web.mapper;

import com.example.concert.config.CustomUserDetails;
import com.example.concert.domain.user.User;
import com.example.concert.domain.user.UserRole;
import com.example.concert.web.dto.UserDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // user 컨트롤러 -> entity
    public static User UserCreateRequestToEntity(UserDto.CreateUserRequest userDto, BCryptPasswordEncoder encoder) {
        return User.builder()
                .email(userDto.getEmail())
                .password(encoder.encode(userDto.getPassword()))
                .phoneNumber(userDto.getPhoneNumber())
                .role(UserRole.getUserRole(userDto.getRole()))
                .build();
    }

    public static User UserCreateRequestToEntity(CustomUserDetails userDetails) {
        return User.builder()
                .id(userDetails.getUserId())
                .email(userDetails.getEmail())
                .phoneNumber(userDetails.getPhoneNumber())
                .role(UserRole.getUserRole(userDetails.getRole()))
                .build();
    }

}
