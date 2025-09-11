package com.example.concert.web.mapper;

import com.example.concert.domain.concert.Concert;
import com.example.concert.service.dto.ConcertResponseDto;
import com.example.concert.web.dto.ConcertDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConcertMapper {
    // Dto -> Entity
    public static Concert ConcertCreateToEntity(ConcertDto.CreateConcertRequest request) {
        return Concert.builder()
                .title(request.getTitle())
                .date(request.getDate())
                .venue(request.getVenue())
                .basePrice(request.getBasePrice())
                .status(request.getStatus())
                .openAt(request.getOpenAt())
                .closeAt(request.getCloseAt())
                .build();
    }

    public static Concert ConcertUpdateToEntity(ConcertDto.UpdateConcertRequest request) {
        return Concert.builder()
                .title(request.getTitle())
                .date(request.getDate())
                .venue(request.getVenue())
                .basePrice(request.getBasePrice())
                .status(request.getStatus())
                .openAt(request.getOpenAt())
                .closeAt(request.getCloseAt())
                .build();
    }

    public static ConcertResponseDto toConcertResponseDto(Concert concert) {
        return ConcertResponseDto.builder()
                .concertId(concert.getConcertId())
                .title(concert.getTitle())
                .date(concert.getDate())
                .venue(concert.getVenue())
                .basePrice(concert.getBasePrice())
                .status(concert.getStatus())
                .openAt(concert.getOpenAt())
                .closeAt(concert.getCloseAt())
                .seats(concert.getSeats().stream()
                        .map(SeatMapper::toSeatResponseDto)
                        .toList())
                .build();
    }

    public static List<ConcertResponseDto> toConcertResponses(List<Concert> concerts) {
        return concerts.stream().map(ConcertMapper::toConcertResponseDto).collect(Collectors.toList());
    }
}
