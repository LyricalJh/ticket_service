package com.example.concert.producer;

import com.example.concert.web.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final static String ORDER_TOPIC = "orders";

    public void send(OrderEvent event) {

        log.info("Order Event 생성 : {}", event);

        kafkaTemplate.send(ORDER_TOPIC, event.getPgTransactionId(), event);
    }
}
