package com.example.concert.consumer;

import com.example.concert.service.notification.NotificationSender;
import com.example.concert.web.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final List<NotificationSender> senders;

    @KafkaListener(topics = "payments", groupId = "notification-service")
    public void consumeConfirmations(PaymentEvent event) {
        senders.forEach(m -> m.sendNotification(event));
    }
}
