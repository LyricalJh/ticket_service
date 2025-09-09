package com.example.concert.batch;

import com.example.concert.queue.EnterQueueService;
import com.example.concert.service.ConcertService;
import com.example.concert.service.dto.ConcertResponseDto;
import com.example.concert.service.notification.KakaoNotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueScheduler {

    private final EnterQueueService enterQueueService;

    private final ConcertService concertService;

    private final KakaoNotificationSender sender;

    @Scheduled(fixedRate = 5000)
    public void process() {

        List<Long> allConcertIds = concertService.getAllConcerts().stream()
                .map(ConcertResponseDto::getConcertId)
                .toList();

        if (!allConcertIds.isEmpty()) {

            Map<Long, List<Long>> allowedByConcert = allConcertIds.stream()
                    .collect(Collectors.toMap(
                        concertId -> concertId,
                        concertId -> enterQueueService.allowUserToEnterQueue(concertId, 100)
                    ));

            allowedByConcert.forEach((concertId, users) -> {
                users.forEach(userId -> {
                    log.info("[Concert {}] 유저 {} → 티켓팅 예약 접수가 10분 정도 남았습니다!", concertId, userId);
                    sender.sendNotification("회원 :" + userId + "Concert  :" + concertId + " 가 티켓팅 예약 접수 10분 정도 남았습니다");
                });
            });
        }
    }
}
