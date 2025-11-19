package com.example.concert.consumer;


import com.example.concert.cache.ReservationCacheService;
import com.example.concert.domain.order.Order;
import com.example.concert.domain.order.OrderRepository;
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
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "orders", groupId = "payment-service")
    public void consumeOrderEvent(OrderEvent event) {
        log.info("📥 주문 이벤트 수신: {}", event);

        PaymentService paymentService = paymentServiceMap.get(event.getPaymentMethod());
        if (paymentService == null) {
            throw new IllegalArgumentException("존재하지 않는 결제방법입니다.");
        }

        try {
            paymentService.pay(event); // 결제 서비스 안에서 PaymentProducer 호출
            //TODO paymentService 결과에 따라 seat 상태를 변경해야함

            Order order = orderRepository.findById(event.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("결재 정보가 존재하지 않습니다."));

            order.markPaid(event.getPgTransactionId());

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            // 성공/실패 관계없이 좌석 점유 해제
            event.getSeatIds().forEach(reservationCacheService::removeOccupySeat);
        }
    }
}

