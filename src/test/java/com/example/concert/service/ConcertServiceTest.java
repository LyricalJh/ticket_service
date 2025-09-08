package com.example.concert.service;

import com.example.concert.domain.concert.Concert;
import com.example.concert.domain.concert.ConcertRepository;
import com.example.concert.domain.concert.ConcertStatus;
import com.example.concert.domain.concert.Seat;
import com.example.concert.service.dto.ConcertResponseDto;
import com.example.concert.web.dto.ConcertDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class ConcertServiceTest {

    @Autowired
    private ConcertService concertService;
    @Autowired
    private ConcertRepository concertRepository;

    @AfterEach
    void tearDown() {
        concertService.deleteConcerts();
    }

    @Test
    @DisplayName("콘서트 데이터 적재 되는것을 확인합니다.")
    void saveConcert() {
        //given
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime afterMonth = now.plusMonths(1);

        ConcertDto.CreateConcertRequest request = ConcertDto.CreateConcertRequest.builder()
                .title("매직바월드")
                .venue("홍대입구역")
                .basePrice(new BigDecimal("10000"))
                .status(ConcertStatus.READY)
                .openAt(now)
                .closeAt(afterMonth)
                .build();

        //when
        Long concertId = concertService.saveConcert(request);
        ConcertResponseDto targetConcert = concertService.getConcertById(concertId);

        //then
        assertThat(targetConcert.getTitle()).isEqualTo("매직바월드");
        assertThat(targetConcert.getVenue()).isEqualTo("홍대입구역");
        assertThat(targetConcert.getBasePrice()).isEqualByComparingTo("10000");
        assertThat(targetConcert.getStatus()).isEqualTo(ConcertStatus.READY);
        assertThat(targetConcert.getOpenAt()).isEqualTo(now);
        assertThat(targetConcert.getCloseAt()).isEqualTo(afterMonth);

    }

    @Test
    @DisplayName("콘서트 목록을 가져오는것을 확인합니다.")
    void getConcertsTest() {
        //given
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime afterMonth = now.plusMonths(1);

        ConcertDto.CreateConcertRequest request1 = ConcertDto.CreateConcertRequest.builder()
                .title("롯데월드")
                .venue("잠실역")
                .basePrice(new BigDecimal("10000"))
                .status(ConcertStatus.READY)
                .openAt(now)
                .closeAt(afterMonth)
                .build();

        ConcertDto.CreateConcertRequest request2 = ConcertDto.CreateConcertRequest.builder()
                .title("애버랜드")
                .venue("애버라인역 부근")
                .basePrice(new BigDecimal("11000"))
                .status(ConcertStatus.OPEN)
                .openAt(now)
                .closeAt(afterMonth)
                .build();

        ConcertDto.CreateConcertRequest request3 = ConcertDto.CreateConcertRequest.builder()
                .title("홍대")
                .venue("홍대입구역")
                .basePrice(new BigDecimal("12000"))
                .status(ConcertStatus.SOLD_OUT)
                .openAt(now)
                .closeAt(afterMonth)
                .build();

        Long l = concertService.saveConcert(request1);
        Long l1 = concertService.saveConcert(request2);
        Long l2 = concertService.saveConcert(request3);

        List<Long> concertIds = new ArrayList<>();
        concertIds.add(l);
        concertIds.add(l1);
        concertIds.add(l2);

        //when
        List<ConcertResponseDto> concerts = concertService.getConcerts(concertIds);

        //then
        assertThat(concerts.size()).isEqualTo(3);
        assertThat(concerts)
                .extracting("title", "venue", "basePrice", "status", "openAt", "closeAt")
                .containsExactlyInAnyOrder(
                        tuple("롯데월드", "잠실역", new BigDecimal("10000.00"), ConcertStatus.READY, now, afterMonth),
                        tuple("애버랜드", "애버라인역 부근", new BigDecimal("11000.00"), ConcertStatus.OPEN, now, afterMonth),
                        tuple("홍대", "홍대입구역", new BigDecimal("12000.00"), ConcertStatus.SOLD_OUT, now, afterMonth)
                );
    }

    @Test
    @DisplayName("콘서트와 좌석 10개가 함께 저장되는지 확인합니다.")
    @Transactional
    void saveConcertWithSeats() {
        // given
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime afterMonth = now.plusMonths(1);

        // 콘서트 생성 요청
        ConcertDto.CreateConcertRequest concertRequest = ConcertDto.CreateConcertRequest.builder()
                .title("올림픽홀 콘서트")
                .venue("올림픽공원")
                .basePrice(new BigDecimal("15000.00"))
                .status(ConcertStatus.OPEN)
                .openAt(now)
                .closeAt(afterMonth)
                .build();

        // 콘서트 저장
        Long concertId = concertService.saveConcert(concertRequest);
        Concert concert = concertRepository.findById(concertId).orElseThrow();

        // 좌석 10개 생성
        for (int i = 1; i <= 10; i++) {
            Seat seat = Seat.builder()
                    .row("A")                  // A열
                    .seatNumber(i)             // 1 ~ 10번
                    .grade("VIP")              // 등급
                    .build();
            concert.getSeats().add(seat);       // Concert와 Seat 매핑
        }

        // when
        Concert loadedConcert = concertRepository.findById(concertId).orElseThrow();

        // then
        assertThat(loadedConcert.getSeats()).hasSize(10);
        assertThat(loadedConcert.getSeats())
                .extracting("row", "seatNumber", "grade")
                .contains(
                        tuple("A", 1, "VIP"),
                        tuple("A", 10, "VIP")
                );
    }

}