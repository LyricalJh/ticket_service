package com.example.concert.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class TokenCacheServiceTest {

    @Autowired
    private TokenCacheService tokenCacheService;

    @Test
    @DisplayName("Redis 토큰 save 잘 저장되고 조회 되는지 확인한다.")
    void saveToken() {
        // given
        String email = "test@example.com";
        String token = "test";

        // when
        tokenCacheService.saveToken(email, token);
        String targetToken = tokenCacheService.getToken(email);

        // then
        assertEquals(token, targetToken);

        // clean up
        tokenCacheService.deleteToken(email);
    }

    @Test
    @DisplayName("사용자 email 기준으로 토큰이 잘 삭제되는지 검증한다.")
    void deleteToken() {
        // given
        String email = "test@example.com";
        String token = "test";

        // when
        tokenCacheService.saveToken(email, token);
        tokenCacheService.deleteToken(email);

        // then
        String resultToken = tokenCacheService.getToken(email);
        assertThat(resultToken).isNull();
    }
}