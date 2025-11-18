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

    @PostMapping("/admin/concert")
    public ApiResponse<?> concertPost(@RequestBody @Valid ConcertDto.CreateConcertRequest request) {
        Long concertId = concertService.saveConcert(request);
        return ApiResponse.ok(concertId, "성공적으로 등록되었습니다.");
    }

    @DeleteMapping("/admin/concert")
    public ApiResponse<?> deleteConcert(@RequestParam @NotEmpty Long concertId) {
        concertService.deleteConcert(concertId);
        return ApiResponse.ok("성공적으로 삭제되었습니다.");
    }

    @PatchMapping("/admin/concert")
    public ApiResponse<?> patchConcert(@RequestBody @Valid ConcertDto.UpdateConcertRequest request) {
        concertService.updateConcert(request);
        return ApiResponse.ok("성공적으로 반영되었습니다.");
    }

    @GetMapping("/concerts")
    public ApiResponse<?> getConcerts(@RequestBody @NotEmpty List<Long> concertIds) {
        List<ConcertResponseDto> concerts = concertService.getConcerts(concertIds);

        return ApiResponse.ok(concertIds, "성공적으로 조회했습니다.");
    }

}
