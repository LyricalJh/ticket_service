package com.example.concert.service;

import com.example.concert.cache.TokenCacheService;
import com.example.concert.common.JwtUtil;
import com.example.concert.domain.user.User;
import com.example.concert.domain.user.UserRepository;
import com.example.concert.web.mapper.UserMapper;
import com.example.concert.web.dto.UserDto;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final TokenCacheService tokenCacheService;

    @Transactional
    public void userRegistered(UserDto.CreateUserRequest request) {
        User user = UserMapper.UserCreateRequestToEntity(request, encoder);

        Boolean exists = userRepository.existsByEmail(user.getEmail());

        if (exists) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        userRepository.save(user);
    }

    public UserDto.TokenResponse getTokens(String refreshToken) throws JwtException {

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String storedRefreshToken = tokenCacheService.getToken(email);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new JwtException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtUtil.createAccessToken(email, role);
        String newRefreshToken = jwtUtil.createRefreshToken(email, role);

        tokenCacheService.saveToken(email, newRefreshToken);

        return new UserDto.TokenResponse(newAccessToken, newRefreshToken);
    }

}
