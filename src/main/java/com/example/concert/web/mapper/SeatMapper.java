package com.example.concert.web.mapper;

import com.example.concert.domain.concert.Seat;
import com.example.concert.service.dto.SeatResponseDto;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public static SeatResponseDto toSeatResponseDto(Seat seat) {
            return SeatResponseDto.builder()
                    .seatId(seat.getId())
                    .section(seat.getSection())
                    .row(seat.getRow())
                    .seatNumber(seat.getSeatNumber())
                    .grade(seat.getGrade())
                    .build();
        }
}
