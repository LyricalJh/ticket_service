package com.example.concert.service;

import com.example.concert.cache.ReservationCacheService;
import com.example.concert.domain.order.Order;
import com.example.concert.domain.order.OrderRepository;
import com.example.concert.web.dto.OrderEvent;
import com.example.concert.web.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final RestTemplate restTemplate = new RestTemplate();

    private final ReservationCacheService reservationCacheService;

    private final OrderRepository orderRepository;

    private static final String TOSS_PAYMENTS_API = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String TEST_SECRET_KEY = "test_sk_xxxxxxxxx"; // 토스 테스트 시크릿 키

    @KafkaListener(topics = "orders", groupId = "payment-service")
    public void consumeOrderEvent(OrderEvent event) {
        log.info("📥 주문 이벤트 수신: {}", event);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(TEST_SECRET_KEY, "");
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("paymentKey", event.getPgTransactionId()); // PG 거래 키
            body.put("orderId", event.getOrderId());
            body.put("amount", event.getTotalAmount());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response =
                    restTemplate.postForEntity(TOSS_PAYMENTS_API, request, String.class);

            log.info("✅ 결제 응답: {}", response.getBody());

            Order orderToEntity = OrderMapper.createOrderToEntity(event);
            orderToEntity.markPaid(orderToEntity.getPgTransactionId());
            orderRepository.save(orderToEntity);

        } catch (Exception e) {
            log.error("❌ 결제 실패", e);
            event.getSeatIds()
                    .forEach(reservationCacheService::removeOccupySeat);
            // Kafka로 PaymentFailedEvent 발행
        }
    }
}
