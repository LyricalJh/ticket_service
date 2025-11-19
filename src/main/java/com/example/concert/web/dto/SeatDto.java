package com.example.concert.web.dto;

import com.example.concert.domain.seat.SeatStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SeatDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateSeatStatusRequest {

        @NotNull(message = "좌석의 ID 값은 필수입니다.")
        private Long id;

        @NotNull(message = "변경될 좌석의 상태값은 필수입니다.")
        private SeatStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateSeatRequest {

        @NotNull(message = "좌석이 속한 콘서트 ID 값은 필수입니다.")
        private Long concertId;

        private String section;

        private String row;

        private Integer seatNumber;

        private String grade;

        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateSeatRequest {

        private Long seatId;

        private String section;

        private String row;

        private Integer seatNumber;

        private String grade;

        private String status;
    }
}
