package com.example.concert.service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertResponseDto {

    private Long concertId;
    private String title;
    private LocalDateTime date;
    private String venue;
    private BigDecimal basePrice;
    private String status;

    private LocalDateTime openAt;
    private LocalDateTime closeAt;

    // 좌석 목록도 같이 내려줄 경우
    @Builder.Default
    private List<SeatResponseDto> seats = new ArrayList<>();
}