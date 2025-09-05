package com.example.concert.web.mapper;

import com.example.concert.domain.order.Order;
import com.example.concert.domain.order.OrderStatus;
import com.example.concert.domain.user.User;
import com.example.concert.web.dto.OrderEvent;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    // Order -> eventDto
    public static OrderEvent createOrderEvent(Order order) {
        return OrderEvent.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .paymentMethod(order.getPaymentMethod())
                .pgTransactionId(order.getPgTransactionId())
                .totalAmount(order.getTotalAmount())
                .build();
    }

    public static Order createOrderToEntity(OrderEvent event) {
        return Order.builder()
                .pgTransactionId(event.getPgTransactionId())
                .id(event.getOrderId())
                .totalAmount(event.getTotalAmount())
                .user(User.builder()
                        .id(event.getUserId())
                        .build())
                .paymentMethod(event.getPaymentMethod())
                .build();
    }
}
