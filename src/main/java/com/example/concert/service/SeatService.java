package com.example.concert.service;

import com.example.concert.domain.concert.Concert;
import com.example.concert.domain.concert.ConcertRepository;
import com.example.concert.domain.seat.Seat;
import com.example.concert.domain.seat.SeatRepository;
import com.example.concert.domain.seat.SeatStatus;
import com.example.concert.web.dto.SeatDto;
import com.example.concert.web.mapper.SeatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ConcertRepository concertRepository;

    @Transactional
    public void updateSeatStatus(Long seatId, SeatStatus status) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석 없음"));
        seat.changeStatus(status);
    }

    @Transactional
    public Long createSeat(SeatDto.CreateSeatRequest request) {

        Concert concert = concertRepository.findById(request.getConcertId())
                  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘서트입니다. id=" + request.getConcertId()));

        Seat seat = SeatMapper.toEntity(request, concert);

        Seat savedSeat = seatRepository.save(seat);

        return savedSeat.getId();
    }

    @Transactional
    public void updateSeat(Long seatId, SeatDto.UpdateSeatRequest request) {

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() ->new IllegalArgumentException("존재하지 않는 좌석입니다. id=" + seatId));

        seat.updateEntityFromDto(request);
    }

    @Transactional
    public void deleteSeat(Long seatId) {

        Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() ->new IllegalArgumentException("존재하지 않는 좌석입니다. id=" + seatId));

        seatRepository.delete(seat);
    }
}
