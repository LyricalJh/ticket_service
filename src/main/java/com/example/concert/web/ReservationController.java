package com.example.concert.web;

import com.example.concert.common.ApiResponse;
import com.example.concert.config.CustomUserDetails;
import com.example.concert.service.ReservationService;
import com.example.concert.web.mapper.UserMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public ApiResponse<?> makeReservation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull Long concertId) {

        reservationService.makeReservation(UserMapper.UserCreateRequestToEntity(userDetails), concertId);
        return ApiResponse.ok("성공적으로 대기열에 등록했습니다.");
    }
}
