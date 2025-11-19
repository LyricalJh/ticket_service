package com.example.concert.web;

import com.example.concert.common.ApiResponse;
import com.example.concert.config.CustomUserDetails;
import com.example.concert.service.ConcertService;
import com.example.concert.service.SeatService;
import com.example.concert.web.dto.ConcertDto;
import com.example.concert.web.dto.SeatDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminController {

    private final ConcertService concertService;
    private final SeatService seatService;

    @GetMapping
    public ApiResponse<?> admin(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.ok(user.getEmail(), "관리자 입장 허용");
    }

    @PostMapping("/concert")
    public ApiResponse<?> concertPost(@RequestBody @Valid ConcertDto.CreateConcertRequest request) {
        Long concertId = concertService.saveConcert(request);
        return ApiResponse.ok(concertId, "성공적으로 등록되었습니다.");
    }

    @PatchMapping("/concert/{concertId}")
    public ApiResponse<?> updateConcert(
            @PathVariable Long concertId,
            @RequestBody @Valid ConcertDto.UpdateConcertRequest request
    ) {
        concertService.updateConcert(concertId, request);
        return ApiResponse.ok("성공적으로 반영되었습니다.");
    }

    @DeleteMapping("/concert/{concertId}")
    public ApiResponse<?> deleteConcert(@PathVariable Long concertId) {
        concertService.deleteConcert(concertId);
        return ApiResponse.ok("성공적으로 삭제되었습니다.");
    }


    @PostMapping("/concert/seat")
    public ApiResponse<?> createSeats(@RequestBody @Valid SeatDto.CreateSeatRequest request) {
        Long seat = seatService.createSeat(request);
        return ApiResponse.ok("좌석 데이터가 성공적으로 등록되었습니다. id=" + seat);
    }

    @PatchMapping("concert/seat/{seatId}")
    public ApiResponse<?> updateSeat(@PathVariable Long seatId, @RequestBody @Valid SeatDto.UpdateSeatRequest request) {
        seatService.updateSeat(seatId, request);
        return ApiResponse.ok("좌석 정보가 성공적으로 수정되었습니다. id=" + seatId);
    }

    @DeleteMapping("concert/seat/{seatId}")
    public ApiResponse<?> deleteSeat(@PathVariable Long seatId) {
        seatService.deleteSeat(seatId);

        return ApiResponse.ok("좌성 정보가 성곡적으로 삭제되었습니다. id=" + seatId);
    }
}
