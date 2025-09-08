package com.example.concert.service.notification;

import com.example.concert.web.dto.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KakaoNotificationSender implements NotificationSender {

    @Override
    public void sendNotification(PaymentEvent event, String status) {
        log.info("카카오 알림톡 발송 {} ", event.getOrderId());
    }
}
