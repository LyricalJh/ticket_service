package com.example.concert.web;

import com.example.concert.common.ApiResponse;
import com.example.concert.service.ConcertService;
import com.example.concert.service.dto.ConcertResponseDto;
import com.example.concert.web.dto.ConcertDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping("/concerts")
    public ApiResponse<?> getConcerts(@RequestBody @NotEmpty List<Long> concertIds) {
        List<ConcertResponseDto> concerts = concertService.getConcerts(concertIds);

        return ApiResponse.ok(concertIds, "성공적으로 조회했습니다.");
    }

}
