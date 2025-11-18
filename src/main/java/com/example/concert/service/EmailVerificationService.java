package com.example.concert.service;

import com.example.concert.domain.user.EmailVerificationToken;
import com.example.concert.domain.user.EmailVerificationTokenRepository;
import com.example.concert.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (verificationToken.isExpired()) {
            throw new IllegalStateException("토큰이 만료되었습니다. 다시 인증 메일을 요청해주세요.");
        }

        if (verificationToken.isUsed()) {
            throw new IllegalStateException("이미 사용된 토큰입니다.");
        }

        User user = verificationToken.getUser();
        user.changeVerified(true);   // set 메서드 없으면 별도 메서드로

        verificationToken.markUsed();
    }
}
