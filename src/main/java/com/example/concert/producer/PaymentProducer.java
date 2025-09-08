package com.example.concert.producer;

import com.example.concert.web.dto.OrderEvent;
import com.example.concert.web.dto.PaymentEvent;
import com.example.concert.web.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final static String PAYMENT_TOPIC = "payments";


    @Retryable(
            value = {Exception.class},      // 어떤 예외에서 재시도할지
            maxAttempts = 3,                  // 최대 재시도 횟수
            backoff = @Backoff(delay = 2000)  // 재시도 간격 (2초)
    )
    public void sendNotification(OrderEvent event, String status) {
        PaymentEvent paymentEvent = PaymentMapper.orderToPaymentEvent(event, status);
        kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        throw new RuntimeException("❌ 알림 전송 실패", ex);
                    }
                });
    }
}
