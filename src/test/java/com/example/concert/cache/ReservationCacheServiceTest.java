package com.example.concert.cache;

import com.example.concert.domain.concert.Concert;
import com.example.concert.domain.concert.ConcertStatus;
import com.example.concert.domain.concert.Seat;
import com.example.concert.domain.order.OrderItem;
import com.example.concert.domain.user.User;
import com.example.concert.domain.user.UserRole;
import com.example.concert.service.policy.DefaultSeatPricingPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
class ReservationCacheServiceTest {

    @Autowired
    private ReservationCacheService reservationCacheService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    public void tearDown() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @Test
    @DisplayName("좌석 선점 테스트")
    public void takenSeatTest() {
        // given
        User user = User.builder()
                .email("test@email.com")
                .phoneNumber("123456789")
                .role(UserRole.USER)
                .build();

        Concert concert = Concert.builder()
                .title("Coldplay World Tour 2025")
                .date(LocalDateTime.of(2025, 12, 1, 19, 0))
                .venue("서울 잠실 주경기장")
                .basePrice(BigDecimal.valueOf(100_000))
                .status(ConcertStatus.OPEN)
                .openAt(LocalDateTime.now().minusDays(1))
                .closeAt(LocalDateTime.now().plusDays(30))
                .updateAt(LocalDateTime.now())
                .createAt(LocalDateTime.now().minusDays(10))
                .build();

        // 좌석에 ID 값 강제로 부여 (테스트용)
        Seat seat1 = Seat.builder()
                .id(1L)
                .section("A구역")
                .row("A열")
                .seatNumber(1)
                .grade("VIP")
                .build();

        Seat seat2 = Seat.builder()
                .id(2L)
                .section("A구역")
                .row("A열")
                .seatNumber(2)
                .grade("R")
                .build();

        Seat seat3 = Seat.builder()
                .id(3L)
                .section("A구역")
                .row("A열")
                .seatNumber(3)
                .grade("S")
                .build();



        List<OrderItem> orderItems =
                OrderItem.createAll(concert, new DefaultSeatPricingPolicy(), Arrays.asList(seat1, seat2, seat3));

        // when & then
        orderItems.forEach(orderItem -> {
            Long seatId = orderItem.getSeat().getId();
            String userId = user.getEmail();

            boolean success = reservationCacheService.occupySeat(seatId, userId, Duration.ofSeconds(10));

            assertThat(success)
                    .as("좌석 %d 은 처음 점유 시도이므로 성공해야 함", seatId)
                    .isTrue();

            String occupant = reservationCacheService.getOccupySeat(seatId);
            assertThat(occupant).isEqualTo(userId);
        });
    }

    @Test
    @DisplayName("동시에 100명이 같은 좌석 점유 시도 -> 한 명만 성공해야 한다")
    void concurrentSeatOccupyTest() throws InterruptedException {
        // given
        Long seatId = 999L; // 테스트용 좌석 ID
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount); // 모든 스레드 준비 완료 신호
        CountDownLatch startLatch = new CountDownLatch(1);           // 동시에 시작하기 위한 신호
        CountDownLatch doneLatch = new CountDownLatch(threadCount);  // 모든 스레드 종료 대기

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            final String userEmail = "user" + i + "@test.com";
            executorService.submit(() -> {
                readyLatch.countDown(); // 준비 완료
                try {
                    startLatch.await(); // 시작 신호 대기
                    boolean success = reservationCacheService.occupySeat(seatId, userEmail, Duration.ofSeconds(10));
                    if (success) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 모든 스레드 준비될 때까지 대기
        readyLatch.await();
        // 동시에 시작!
        startLatch.countDown();
        // 모든 스레드 종료 대기
        doneLatch.await();

        executorService.shutdown();

        // then
        assertThat(successCount.get())
                .as("좌석은 단 1명만 점유 성공해야 함")
                .isEqualTo(1);

        assertThat(failCount.get())
                .as("나머지 99명은 실패해야 함")
                .isEqualTo(99);

        String occupant = reservationCacheService.getOccupySeat(seatId);
        assertThat(occupant).isNotNull();
    }

    @Test
    @DisplayName("좌석 점유 TTL 만료 후 자동 해제되는지 테스트")
    void seatOccupyExpireTest() throws InterruptedException {
        // given
        Long seatId = 1L;
        String userEmail = "expire@test.com";

        // 2초 TTL로 점유
        boolean success = reservationCacheService.occupySeat(seatId, userEmail, Duration.ofSeconds(2));
        assertThat(success).isTrue();

        String occupant = reservationCacheService.getOccupySeat(seatId);
        assertThat(occupant).isEqualTo(userEmail);

        // when: TTL(2초) + 여유 시간(1초) 만큼 대기
        Thread.sleep(3000);

        // then: 자동 해제되어야 함
        String afterExpire = reservationCacheService.getOccupySeat(seatId);
        assertThat(afterExpire).isNull();
    }
}
