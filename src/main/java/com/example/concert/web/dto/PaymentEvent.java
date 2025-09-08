package com.example.concert.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {

    private Long orderId;
    private Long userId;
    private String userName;
    private String userEmail;
    private BigDecimal totalAmount;
    private String pgTransactionId;

    private String paymentStatus;
}
