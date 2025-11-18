package com.example.concert.domain.order;

import com.example.concert.domain.concert.Concert;
import com.example.concert.domain.seat.Seat;
import com.example.concert.service.policy.SeatPricingPolicy;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 콘서트에 대한 티켓인가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    // 어떤 주문에 속했는가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 어떤 좌석을 예매했는가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    private BigDecimal price;

    public void changeOrder(Order order) {
        this.order = order;
    }

    public static OrderItem create(Order order, Concert concert, Seat seat, SeatPricingPolicy policy) {
        return OrderItem.builder()
                .seat(seat)
                .order(order)
                .concert(concert)
                .price(policy.calculatePrice(concert, seat))
                .build();
    }

    public static List<OrderItem> createAll(
            Order order,
            Concert concert,
            SeatPricingPolicy policy,
            List<Seat> seats
    ) {
        return seats.stream()
                .map(seat -> create(order, concert, seat, policy))
                .collect(Collectors.toList());
    }

}