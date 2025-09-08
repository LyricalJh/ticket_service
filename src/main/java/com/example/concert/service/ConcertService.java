package com.example.concert.service;

import com.example.concert.cache.ConcertCacheService;
import com.example.concert.domain.concert.Concert;
import com.example.concert.domain.concert.ConcertRepository;
import com.example.concert.service.dto.ConcertResponseDto;
import com.example.concert.web.dto.ConcertDto;
import com.example.concert.web.mapper.ConcertMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertRepository concertRepository;

    private final ConcertCacheService concertCacheService;

    @Transactional
    public Long saveConcert(ConcertDto.CreateConcertRequest request) {
        Concert concert = ConcertMapper.ConcertCreateToEntity(request);
        Concert savedConcert = concertRepository.save(concert);

        concertCacheService.putConcert(ConcertMapper.toConcertResponseDto(savedConcert), Duration.ofMinutes(5));
        return savedConcert.getConcertId();
    }

    public ConcertResponseDto getConcertById(Long concertId) {

        ConcertResponseDto cachedConcert = concertCacheService.getConcert(concertId);

        if (cachedConcert != null) {
            return cachedConcert;
        }

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalStateException("해당 콘서트는 존재하지 않는 콘서트입니다."));
        ConcertResponseDto dto = ConcertMapper.toConcertResponseDto(concert);
        concertCacheService.putConcert(dto, Duration.ofMinutes(5));
        return dto;
    }

    public List<ConcertResponseDto> getConcerts(List<Long> concertIds) {

        if (concertIds == null || concertIds.isEmpty()) {
            throw new IllegalArgumentException("concertIds가 비어있습니다. 최소 1개 이상의 ID가 필요합니다.");
        }

        return ConcertMapper.toConcertResponses(concertRepository.findAllById(concertIds));
    }

    @Transactional
    public void deleteConcert(Long concertId) {

        if (concertId == null) {
            throw new IllegalArgumentException("concertId는 null일 수 없습니다.");
        }

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 콘서트입니다."));

        concertRepository.delete(concert);
        concertCacheService.evictConcert(concertId);
    }

    public void updateConcert(ConcertDto.UpdateConcertRequest request) {
        //TODO dirty check 방식으로 수정하는게 더 올바름
        if (request == null) {
            throw new IllegalArgumentException("존재하지 않는 콘서트입니다.");
        }

        Concert savedConcert = concertRepository.save(ConcertMapper.ConcertUpdateToEntity(request));
        concertCacheService.putConcert(ConcertMapper.toConcertResponseDto(savedConcert), Duration.ofMinutes(5));
    }

    @Transactional
    public void deleteConcerts() {
        concertRepository.deleteAll();
        //TODO  캐쉬 삭제 필요함
    }


}
