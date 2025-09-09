package com.example.concert.service;

import com.example.concert.domain.user.User;
import com.example.concert.queue.EnterQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final EnterQueueService enterQueueService;

    public void makeReservation(User user, Long concertId) {
        enterQueueService.enterQueue(concertId, user.getId());
    }
}
