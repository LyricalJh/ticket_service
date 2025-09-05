package com.example.concert.cache;

import com.example.concert.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "USER:";
    private static final long USER_TTL = 60 * 30; // 30분 캐시

    public void saveUser(User user) {
        redisTemplate.opsForValue().set(
                PREFIX + user.getEmail(),
                user,
                USER_TTL,
                TimeUnit.SECONDS
        );
    }

    public User getUser(String email) {
        Object obj = redisTemplate.opsForValue().get(PREFIX + email);
        if (obj instanceof User) {
            return (User) obj;
        } else {
            log.warn("유저 객체화 실패 = {}", obj);
        }
        return null;
    }

    public void deleteUser(String email) {
        redisTemplate.delete(PREFIX + email);
    }
}
