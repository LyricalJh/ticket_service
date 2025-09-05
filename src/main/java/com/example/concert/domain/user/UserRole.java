package com.example.concert.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("관리자", "ROLE_ADMIN"),
    USER("사용자", "ROLE_USER");

    private final String description;
    private final String code;

    UserRole(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public static UserRole getUserRole(String code) {
           return java.util.Arrays.stream(values())
                   .filter(each -> each.code.equals(code))
                   .findFirst()
                   .orElseThrow(() ->
                           new IllegalArgumentException("해당 권한은 존재하지 않습니다: " + code));
       }
}
