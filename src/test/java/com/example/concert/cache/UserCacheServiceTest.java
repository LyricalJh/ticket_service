package com.example.concert.cache;

import com.example.concert.domain.user.User;
import com.example.concert.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class UserCacheServiceTest {

    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;

    @Autowired
    private UserCacheService userCacheService;

    @Test
    @DisplayName("Redis set/get 동작테스트")
    void testSetAndGetRedisValue() {
        // given
        String key = "user:test:email";
        String value = UUID.randomUUID().toString();

        // when
        objectRedisTemplate.opsForValue().set(key, value);
        Object result = objectRedisTemplate.opsForValue().get(key);

        // then
        assertThat(result).isInstanceOf(String.class);
        assertThat((String) result).isEqualTo(value);

        // clean up
        objectRedisTemplate.delete(key);
    }

    @Test
    @DisplayName("특정 key 값으로 값을 삭제하면 조회되면 안된다.")
    void testDeleteRedisValueWithKey() {
        // given
        String key = "user:test:email";
        String value = UUID.randomUUID().toString();

        // when
        objectRedisTemplate.opsForValue().set(key, value);
        objectRedisTemplate.delete(key);

        // then
        Object result = objectRedisTemplate.opsForValue().get(key);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Redis 통합 테스트 saveUser / getUser")
    void redisSaveAndGetUserTest() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("1234")
                .role(UserRole.USER)
                .build();

        // when
        userCacheService.saveUser(user);
        User cachedUser = userCacheService.getUser(user.getEmail());


        // then
        assertThat(cachedUser).isNotNull();
        assertThat(cachedUser.getEmail()).isEqualTo(user.getEmail());

        // clean up
        userCacheService.deleteUser(cachedUser.getEmail());
        assertThat(userCacheService.getUser("test@example.com")).isNull();
    }
}
