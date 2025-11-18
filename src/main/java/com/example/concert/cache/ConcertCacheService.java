package com.example.concert.cache;


import com.example.concert.service.dto.ConcertResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConcertCacheService {

    private final RedisTemplate<String, Object> objectRedisTemplate;

    private static final String CONCERT_PREFIX = "concert:";

    private String getKey(Long concertId) {
        return CONCERT_PREFIX + concertId;
    }

    // 캐시 저장
    public void putConcert(ConcertResponseDto concert, Duration ttl) {
        String key = getKey(concert.getConcertId());
        objectRedisTemplate.opsForValue().set(key, concert, ttl);
        log.info("Concert 캐싱 완료: {}", key);
    }

    // 캐시 조회
    public ConcertResponseDto getConcert(Long concertId) {
        String key = getKey(concertId);
        Object cached = objectRedisTemplate.opsForValue().get(key);
        if (cached instanceof ConcertResponseDto dto) {
            return dto;
        }
        return null;
    }

    // 캐시 삭제
    public void evictConcert(Long concertId) {
        String key = getKey(concertId);
        objectRedisTemplate.delete(key);
        log.info("Concert 캐시 삭제: {}", key);
    }

}

