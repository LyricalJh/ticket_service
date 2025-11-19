package com.example.concert.web.mapper;

import com.example.concert.domain.concert.Concert;
import com.example.concert.domain.seat.Seat;
import com.example.concert.domain.seat.SeatStatus;
import com.example.concert.service.dto.SeatResponseDto;
import com.example.concert.web.dto.SeatDto;
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

    public static Seat toEntity(SeatDto.CreateSeatRequest request, Concert concert) {
        return Seat.builder()
                .section(request.getSection())
                .row(request.getRow())
                .seatNumber(request.getSeatNumber())
                .grade(request.getGrade())
                .status(convertStatus(request.getStatus()))
                .concert(concert)
                .build();
    }

    private static SeatStatus convertStatus(String status) {
        if (status == null || status.isBlank()) {
            return SeatStatus.AVAILABLE; // 기본값
        }
        return SeatStatus.valueOf(status.toUpperCase());
    }

}
