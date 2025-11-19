package com.example.concert.web;

import com.example.concert.common.ApiResponse;
import com.example.concert.config.CustomUserDetails;
import com.example.concert.service.EmailVerificationService;
import com.example.concert.service.UserService;
import com.example.concert.web.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/join")
    public ApiResponse<?> join(@RequestBody @Valid UserDto.CreateUserRequest user) {
        userService.userRegistered(user);
        return new ApiResponse<>(user, "성공적으로 회원가입을 완료했습니다.", HttpStatus.OK);
    }

    @GetMapping("/")
    public ApiResponse<?> mainPage(@AuthenticationPrincipal CustomUserDetails user) {
        return new ApiResponse<>(user.getEmail(), "환영합니다", HttpStatus.OK);
    }


    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader("X-Refresh-Token") String refreshToken) {
        try {
            UserDto.TokenResponse tokens = userService.getTokens(refreshToken);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\":\"Invalid refresh token\"}");
        }
    }

    @GetMapping("/verify")
    public ApiResponse<?> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ApiResponse.ok("이메일 인증이 완료되었습니다.");
    }

}
