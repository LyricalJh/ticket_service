package com.example.concert.cache;

import com.example.concert.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCacheService {

    private final RedisTemplate<String, Object> objectRedisTemplate;

    private final ObjectMapper objectMapper;

    private static final String PREFIX = "USER:";
    private static final long USER_TTL = 60 * 30; // 30분 캐시

    public void saveUser(User user) {
        objectRedisTemplate.opsForValue().set(
                PREFIX + user.getEmail(),
                user,
                USER_TTL,
                TimeUnit.SECONDS
        );
    }

    public User getUser(String email) {
        Object obj = objectRedisTemplate.opsForValue().get(PREFIX + email);
        if (obj == null) return null;

        if (obj instanceof User) {
            return (User) obj;
        } else {
            // LinkedHashMap → User 변환
            return objectMapper.convertValue(obj, User.class);
        }
    }

    public void deleteUser(String email) {
        objectRedisTemplate.delete(PREFIX + email);
    }
}
