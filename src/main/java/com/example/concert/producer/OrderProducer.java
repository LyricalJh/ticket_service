package com.example.concert.producer;

import com.example.concert.web.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final static String ORDER_TOPIC = "orders";

    public void sendOrderEvent(OrderEvent event) {
        kafkaTemplate.send(ORDER_TOPIC, event.getPgTransactionId(), event);
    }

}
