package com.example.concert.service;

import com.example.concert.domain.concert.ConcertStatus;
import com.example.concert.domain.user.User;
import com.example.concert.queue.EnterQueueService;
import com.example.concert.service.dto.ConcertResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final EnterQueueService enterQueueService;

    private final ConcertService concertService;

    public void makeReservation(User user, Long concertId) {

        ConcertResponseDto concertById = concertService.getConcertById(concertId);

        if (concertById.getStatus() != ConcertStatus.OPEN ||
            concertById.getOpenAt().isAfter(LocalDateTime.now()) ||
            concertById.getCloseAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("현재 예매할 수 없는 콘서트입니다.");
        }

        enterQueueService.enterQueue(concertById.getConcertId(), user.getId());
    }
}
