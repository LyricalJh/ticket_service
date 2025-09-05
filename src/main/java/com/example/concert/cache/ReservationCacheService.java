package com.example.concert.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReservationCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    private final static String SEAT_LOCK_PREFIX = "seat:lock:";

    public boolean occupySeat(Long seatId, String userEmail, Duration ttl) {
        String key = SEAT_LOCK_PREFIX + seatId;

        Boolean success = redisTemplate.opsForValue().setIfAbsent(
             key,
             userEmail,
             ttl.toMillis(),
             TimeUnit.MILLISECONDS
     );

        return Boolean.TRUE.equals(success);
    }

    public String getOccupySeat(Long seatId) {
        return redisTemplate.opsForValue().get(SEAT_LOCK_PREFIX + seatId);
    }

    public boolean areSeatsOccupied(List<Long> seatIds) {
        return seatIds.stream()
                .map(id -> redisTemplate.opsForValue().get(SEAT_LOCK_PREFIX + id))
                .anyMatch(Objects::nonNull);
    }

    public void removeOccupySeat(Long seatId) {
        redisTemplate.delete(SEAT_LOCK_PREFIX + seatId);
    }
}
