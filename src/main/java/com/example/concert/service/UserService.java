package com.example.concert.service;

import com.example.concert.cache.TokenCacheService;
import com.example.concert.cache.UserCacheService;
import com.example.concert.common.JwtUtil;
import com.example.concert.domain.user.EmailVerificationToken;
import com.example.concert.domain.user.EmailVerificationTokenRepository;
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

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final TokenCacheService tokenCacheService;
    private final UserCacheService userCacheService;
    
    private final EmailVerificationTokenRepository tokenRepository;
    private final MailService mailService;

    @Transactional
    public void userRegistered(UserDto.CreateUserRequest request) {
        User user = UserMapper.UserCreateRequestToEntity(request, encoder);

        Boolean exists = userRepository.existsByEmail(user.getEmail());

        if (exists) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        User saved = userRepository.save(user);

        // 2. 토큰 생성
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(saved)
                .expiresAt(LocalDateTime.now().plusHours(24)) // 24시간 유효
                .build();

        tokenRepository.save(verificationToken);

        // 3. 이메일 발송
        mailService.sendVerificationMail(saved.getEmail(), token);
    }

    public User getUserByEmail(String email) {
        User cachedUser = userCacheService.getUser(email);
        if (cachedUser != null) {
            return cachedUser;
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
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
