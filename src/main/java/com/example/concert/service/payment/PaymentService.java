package com.example.concert.service.payment;

import com.example.concert.web.dto.OrderEvent;

public interface PaymentService {

    public String pay(OrderEvent event) throws Exception;

}
