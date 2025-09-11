package com.example.concert.service.payment;

import com.example.concert.web.dto.OrderEvent;

public interface PaymentService {

    void pay(OrderEvent event);
}
