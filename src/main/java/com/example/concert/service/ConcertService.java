package com.example.concert.service;

import com.example.concert.domain.concert.Concert;
import com.example.concert.domain.concert.ConcertRepository;
import com.example.concert.service.dto.ConcertResponseDto;
import com.example.concert.web.dto.ConcertDto;
import com.example.concert.web.mapper.ConcertMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;

    @Transactional
    public Long saveConcert(ConcertDto.CreateConcertRequest request) {
        Concert concert = ConcertMapper.ConcertCreateToEntity(request);
        Concert savedConcert = concertRepository.save(concert);
        return savedConcert.getConcertId();
    }

    public ConcertResponseDto getConcertById(Long concertId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalStateException("해당 콘서트는 존재하지 않는 콘서트입니다."));
        return ConcertMapper.toConcertResponseDto(concert);
    }

    public List<ConcertResponseDto> getConcerts() {
        return ConcertMapper.toConcertResponses(concertRepository.findAll());
    }

    @Transactional
    public void deleteConcerts() {
        concertRepository.deleteAll();
    }


}
