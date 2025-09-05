package com.example.concert.service;

import com.example.concert.domain.concert.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public Long saveSeat() {
        return 0L;
    }


}
