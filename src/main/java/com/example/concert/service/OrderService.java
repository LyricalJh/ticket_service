package com.example.concert.service;

import com.example.concert.cache.ReservationCacheService;
import com.example.concert.domain.concert.Concert;
import com.example.concert.domain.concert.ConcertRepository;
import com.example.concert.domain.concert.Seat;
import com.example.concert.domain.concert.SeatRepository;
import com.example.concert.domain.order.Order;
import com.example.concert.domain.order.OrderItem;
import com.example.concert.domain.order.OrderRepository;
import com.example.concert.domain.user.User;
import com.example.concert.service.policy.SeatPricingPolicy;
import com.example.concert.web.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final ReservationCacheService reservationCacheService;

    private final ConcertRepository concertRepository;

    private final SeatRepository seatRepository;

    private final SeatPricingPolicy seatPricingPolicy;

    private final OrderProducer orderProducer;

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(User user, Long concertId, List<Long> seatIds) {

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트가 존재하지 않습니다."));

        boolean isAnyOccupied = reservationCacheService.areSeatsOccupied(seatIds);

        if (isAnyOccupied) {
            throw new IllegalStateException("선택하신 좌석은 이미 선점된 좌석으로 구매가 불가능합니다.");
        }

        List<Seat> seats = seatRepository.findAllById(seatIds);
        List<OrderItem> orderItems = OrderItem.createAll(concert, seatPricingPolicy, seats);

        Order order = Order.CreateOrder(user, orderItems);
        orderProducer.sendOrderEvent(OrderMapper.createOrderEvent(order));

        return orderRepository.save(order);
    }
}
