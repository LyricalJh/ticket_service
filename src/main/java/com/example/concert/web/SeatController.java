package com.example.concert.web;

import com.example.concert.domain.concert.SeatStatus;
import com.example.concert.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SeatController {

    private final SeatService seatService;

    @PatchMapping("/seats/{id}/status")
    public ResponseEntity<Void> updateSeatStatus(@PathVariable Long id,
                                                 @RequestParam String status) {
        SeatStatus seatStatus = SeatStatus.valueOf(status.toUpperCase());

        seatService.updateSeatStatus(id, seatStatus);
        return ResponseEntity.ok().build();
    }
}
