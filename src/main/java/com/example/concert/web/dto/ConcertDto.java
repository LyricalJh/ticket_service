package com.example.concert.web.dto;

import com.example.concert.domain.concert.ConcertStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ConcertDto {

    /**
     * 콘서트 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateConcertRequest {

        @NotNull(message = "콘서트명은 필수값입니다.")
        private String title;

        @NotNull(message = "공연일자는 필수값입니다.")
        private LocalDateTime date;

        @NotNull(message = "공연장소는 필수값입니다.")
        private String venue;

        @NotNull(message = "기본가격은 필수값입니다.")
        private BigDecimal basePrice;

        @NotNull(message = "상태는 필수값입니다.")
        private ConcertStatus status;

        private LocalDateTime openAt;
        private LocalDateTime closeAt;
    }

    /**
     * 콘서트 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateConcertRequest {

        @NotNull(message = "콘서트 ID는 필수값입니다.")
        private Long concertId;

        private String title;
        private LocalDateTime date;
        private String venue;
        private BigDecimal basePrice;
        private ConcertStatus status;
        private LocalDateTime openAt;
        private LocalDateTime closeAt;
    }

    /**
     * 콘서트 조회 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConcertResponse {

        private Long concertId;
        private String title;
        private LocalDateTime date;
        private String venue;
        private BigDecimal basePrice;
        private ConcertStatus status;
        private LocalDateTime openAt;
        private LocalDateTime closeAt;
        private LocalDateTime updateAt;
        private LocalDateTime createAt;
    }

    /**
     * 콘서트 삭제 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteConcertRequest {

        @NotNull(message = "콘서트 ID는 필수값입니다.")
        private Long concertId;
    }
}
