package com.example.concert.service.dto;

import com.example.concert.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MyTicketDto(
        Long orderId,
        Long orderItemId,
        Long concertId,
        String concertTitle,
        LocalDateTime concertAt,
        String venue,
        String seatLabel,
        BigDecimal price,
        OrderStatus orderStatus
) { }