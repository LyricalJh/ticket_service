package com.example.concert.web.mapper;

import com.example.concert.web.dto.OrderEvent;
import com.example.concert.web.dto.PaymentEvent;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    // Order -> Payment
    public static PaymentEvent orderToPaymentEvent(OrderEvent event) {
        return PaymentEvent.builder()
                .orderId(event.getOrderId())
                .totalAmount(event.getTotalAmount())
                .pgTransactionId(event.getPgTransactionId())
                .userName(event.getUserName())
                .userEmail(event.getUserEmail())
                .userId(event.getUserId())
                .build();
    }
}
