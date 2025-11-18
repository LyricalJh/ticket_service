package com.example.concert.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenCacheService {

    private static final String PREFIX = "RT:";

    @Value("${jwt.refresh-expiration}") // ms 단위
    private long refreshExpirationMs;

    private final StringRedisTemplate stringRedisTemplate;


    /**
     * Refresh Token 저장 (key: RT:username, value: token)
     */
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2) // 200ms → 400ms → 800ms
    )
    public void saveToken(String email, String token) {
        stringRedisTemplate.opsForValue().set(
                PREFIX + email,
                token,
                refreshExpirationMs,
                TimeUnit.MILLISECONDS
        );
        log.info("✅ RefreshToken 저장: key={}, ttl={}s", PREFIX + email, refreshExpirationMs);
    }

    @Recover // 파라미터 시그니처 saveToken 과 동일
    public void recover(Exception e, String email, String token) {
        log.error("❌ Redis 저장 실패 (DLQ/알람 필요) - email={}, token={}", email, token, e);
        //TODO 모니터링 시스템 연계
    }

    /**
     * Refresh Token 조회
     */
    public String getToken(String email) {
        return stringRedisTemplate.opsForValue().get(PREFIX + email);
    }

    /**
     * Refresh Token 삭제 (로그아웃 시)
     */
    public void deleteToken(String username) {
        stringRedisTemplate.delete(PREFIX + username);
        log.info("🗑️ RefreshToken 삭제: key={}", PREFIX + username);
    }
}
