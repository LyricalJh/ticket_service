package com.example.concert.domain.order;

import com.example.concert.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // CREATED, PAID, FAILED, CANCELLED

    private BigDecimal totalAmount;

    private String pgTransactionId; // PG사 거래 ID
    private String paymentMethod;   // 카드, 계좌이체 등

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.changeOrder(this);
    }

    public void addOrderItems(List<OrderItem> items) {
        for (OrderItem item : items) {
            addOrderItem(item);
        }
    }

    public void markPaid(String pgTransactionId) {
        this.status = OrderStatus.PAID;
        this.pgTransactionId = pgTransactionId;
        this.paidAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public static Order createOrder(User user) {
        return Order.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .build();
    }
}