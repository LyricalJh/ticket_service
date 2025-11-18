package com.example.concert.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReservationCacheService {

    private final StringRedisTemplate stringRedisTemplate;

    private final static String SEAT_LOCK_PREFIX = "seat:lock:";

    public boolean occupySeat(Long seatId, String userEmail, Duration ttl) {
        String key = SEAT_LOCK_PREFIX + seatId;

        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(
                key,
                userEmail,
                ttl.toMillis(),
                TimeUnit.MILLISECONDS
        );

        return Boolean.TRUE.equals(success);
    }

    public String getOccupySeat(Long seatId) {
        return stringRedisTemplate.opsForValue().get(SEAT_LOCK_PREFIX + seatId);
    }

    public boolean isSeatOccupied(Long seatId) {
        return stringRedisTemplate.opsForValue().get(SEAT_LOCK_PREFIX + seatId) != null;
    }

    public void removeOccupySeat(Long seatId) {
        stringRedisTemplate.delete(SEAT_LOCK_PREFIX + seatId);
    }
}
