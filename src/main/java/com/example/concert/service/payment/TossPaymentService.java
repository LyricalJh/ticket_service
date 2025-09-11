package com.example.concert.service.payment;

import com.example.concert.producer.PaymentProducer;
import com.example.concert.web.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TossPaymentService implements PaymentService {
    private final RestTemplate restTemplate;

    private final PaymentProducer paymentProducer;

    private static final String TOSS_PAYMENTS_API = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String TEST_SECRET_KEY = "test_sk_xxxxxxxxx"; // 토스 테스트 시크릿 키


    @Override
    public void pay(OrderEvent event) {
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

        //TODO 결제 응답에 따라 메세지 내용 수정
        paymentProducer.send(event);
    }

}
