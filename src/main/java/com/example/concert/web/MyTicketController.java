package com.example.concert.web;

import com.example.concert.common.ApiResponse;
import com.example.concert.config.CustomUserDetails;
import com.example.concert.service.TicketQueryService;
import com.example.concert.service.dto.MyTicketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/me")
public class MyTicketController {

    private final TicketQueryService ticketQueryService;

    @GetMapping("/tickets")
    public ApiResponse<?> getMyTickets(@AuthenticationPrincipal CustomUserDetails userDetails) {

        // ⚠️ 여기서 userId 꺼내는 메서드는 실제 CustomUserDetails 구현에 맞게 수정 필요
        Long userId = userDetails.getUserId(); // or getId(), getUser().getId() 등

        List<MyTicketDto> tickets = ticketQueryService.getMyTickets(userId);
        return ApiResponse.ok(tickets, "내 티켓 목록을 조회했습니다.");
    }
}
