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
    public static class SeatStatusUpdateRequest {

        @NotNull(message = "좌석의 ID 값은 필수입니다.")
        private Long id;

        @NotNull(message = "변경될 좌석의 상태값은 필수입니다.")
        private SeatStatus status;
    }
}
