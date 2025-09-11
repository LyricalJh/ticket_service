package com.example.concert.service;

import com.example.concert.domain.concert.Seat;
import com.example.concert.domain.concert.SeatRepository;
import com.example.concert.domain.concert.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public Long saveSeat() {
        return 0L;
    }

    public void updateSeatStatus(Long seatId, SeatStatus status) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석 없음"));
        seat.changeStatus(status);
    }
}
