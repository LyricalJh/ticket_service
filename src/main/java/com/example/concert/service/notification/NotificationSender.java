package com.example.concert.service.notification;

import com.example.concert.web.dto.PaymentEvent;

public interface NotificationSender {
    void sendNotification(PaymentEvent event, String status);
}
