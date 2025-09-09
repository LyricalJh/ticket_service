package com.example.concert.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EnterQueueService {

    private final RedisTemplate<String, Object> redisTemplate;

    private String queueKey(Long concertId) {
        return "concert:" + concertId + ":queue";
    }

    private String activeKey(Long concertId) {
        return "concert:" + concertId + ":active_users";
    }

    // 대기열 등록
    public void enterQueue(Long concertId, Long userId) {
        redisTemplate.opsForZSet().add(queueKey(concertId), userId, System.currentTimeMillis());
    }

    // 순번 조회 실시간 조회는 사용하지 않고
    public Long getQueuePosition(Long concertId, Long userId) {
        Long rank = redisTemplate.opsForZSet().rank(queueKey(concertId), userId);
        return rank != null ? rank + 1 : null;
    }

    // N명 입장 허용
    public List<Long> allowUserToEnterQueue(Long concertId, int batchSize) {
        Set<Object> users = redisTemplate.opsForZSet().range(queueKey(concertId), 0, batchSize - 1);

        if (users == null || users.isEmpty()) {
            return List.of();
        }

        for (Object user : users) {
            redisTemplate.opsForSet().add(activeKey(concertId), user);
            redisTemplate.opsForZSet().remove(queueKey(concertId), user);
        }

        return users.stream().map(u -> Long.valueOf(u.toString())).toList();
    }

    public void validUserAccess(Long concertId, Long userId) {
        Boolean isActive = redisTemplate.opsForSet().isMember(activeKey(concertId), userId);

        if (!Boolean.TRUE.equals(isActive)) {
            throw new IllegalStateException("대기열에 있는 유저만 예매할 수 있습니다.");
        }
    }

    public boolean isInQueue(Long concertId, Long userId) {
        return redisTemplate.opsForZSet().rank(queueKey(concertId), userId) != null;
    }

    public boolean isInActiveUsers(Long concertId, Long userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(activeKey(concertId), userId));
    }
}

