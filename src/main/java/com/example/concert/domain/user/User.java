package com.example.concert.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA 기본 생성자 (필수)
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더와 함께 쓰기 좋음
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;
    private String name;
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Builder.Default
    @Column(name = "email_verified")
    private boolean emailVerified = false;

    public void changeVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

}
