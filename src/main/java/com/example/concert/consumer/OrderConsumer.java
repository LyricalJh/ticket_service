package com.example.concert.consumer;


import com.example.concert.cache.ReservationCacheService;
import com.example.concert.service.payment.PaymentService;
import com.example.concert.web.dto.OrderEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final ReservationCacheService reservationCacheService;
    private final Map<String, PaymentService> paymentServiceMap;

    @KafkaListener(topics = "orders", groupId = "payment-service")
    public void consumeOrderEvent(OrderEvent event) {
        log.info("📥 주문 이벤트 수신: {}", event);

        PaymentService paymentService = paymentServiceMap.get(event.getPaymentMethod());
        if (paymentService == null) {
            throw new IllegalArgumentException("존재하지 않는 결제방법입니다.");
        }

        try {
            paymentService.pay(event); // 결제 서비스 안에서 PaymentProducer 호출
            //TODO 해당 좌석 상태를 예매 상태로 변경 해야함...

        } finally {
            // 성공/실패 관계없이 좌석 점유 해제
            event.getSeatIds().forEach(reservationCacheService::removeOccupySeat);
        }
    }
}

