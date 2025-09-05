package com.example.concert.service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponseDto {

    private Long seatId;
    private String section;
    private String row;
    private Integer seatNumber;
    private String grade;
}