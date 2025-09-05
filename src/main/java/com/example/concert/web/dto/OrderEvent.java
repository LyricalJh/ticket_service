package com.example.concert.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {
    private String pgTransactionId;
    private String paymentMethod;

    private Long orderId;
    private Long userId;
    private Long concertId;
    private List<Long> seatIds;
    private BigDecimal totalAmount;
}
