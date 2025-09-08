package com.example.concert.consumer;


import com.example.concert.cache.ReservationCacheService;
import com.example.concert.domain.order.Order;
import com.example.concert.domain.order.OrderRepository;
import com.example.concert.producer.PaymentProducer;
import com.example.concert.service.payment.PaymentService;
import com.example.concert.web.dto.OrderEvent;

import com.example.concert.web.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final PaymentProducer paymentProducer;

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
            String response = paymentService.pay(event);

            //TODO RESPONSE 올바르면 결재 SAVE

            Order orderToEntity = OrderMapper.createOrderToEntity(event);
            orderToEntity.markPaid(orderToEntity.getPgTransactionId());

            orderRepository.save(orderToEntity);
            paymentProducer.sendNotification(event, "SUCCESS");

        } catch (Exception e) {
            paymentProducer.sendNotification(event, "FAIL");
            event.getSeatIds()
                    .forEach(reservationCacheService::removeOccupySeat);
        }
    }
}
