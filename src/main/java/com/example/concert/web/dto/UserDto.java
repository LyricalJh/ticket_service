package com.example.concert.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
public class UserDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {

        @NotNull(message = "유저명은 필수값입니다.")
        private String username;

        @NotNull(message = "이메일값은 필수값입니다.")
        private String email;

        @NotNull(message = "비밀번호는 필수값입니다.")
        private String password;

        @NotNull(message = "연락처는 필수값입니다.")
        private String phoneNumber;

        private String role;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {

        @NotNull(message = "회원ID는 필수값입니다.")
        private Long id;

        @NotNull(message = "수정할 이메일 값은 필수입니다.")
        private String email;

        @NotNull(message = "비밀번호는 필수값입니다.")
        private String password;

        @NotNull(message = "수정할 핸드폰번호는 필수 입니다.")
        private String phoneNumber;

        private String role;
    }

    @Data
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }
}
