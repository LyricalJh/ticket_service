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
import com.example.concert.producer.OrderProducer;
import com.example.concert.queue.EnterQueueService;
import com.example.concert.service.policy.SeatPricingPolicy;
import com.example.concert.web.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    private final EnterQueueService enterQueueService;

    @Transactional
    public Order createOrder(User user, Long concertId, List<Long> seatIds) {
        // 1. 콘서트 조회
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트가 존재하지 않습니다."));

        // 2. 대기열 확인
        boolean inQueue = enterQueueService.isInQueue(concertId, user.getId());
        boolean inActive = enterQueueService.isInActiveUsers(concertId, user.getId());

        if (!inQueue && !inActive) {
            // 대기열에 자동 등록
            enterQueueService.enterQueue(concertId, user.getId());
            throw new IllegalStateException("대기열에 등록되었습니다. 잠시 후 입장이 가능합니다.");
        }

        if (!inActive) {
            throw new IllegalStateException("아직 입장 차례가 아닙니다. 대기열 순번을 확인해주세요.");
        }

        // 3. 좌석 조회
        List<Seat> seats = seatRepository.findAllById(seatIds);

        // 4. 좌석 점유 확인
        List<Seat> occupiedSeats = seats.stream()
                .filter(seat -> reservationCacheService.isSeatOccupied(seat.getId()))
                .toList();

        if (!occupiedSeats.isEmpty()) {
            throw new IllegalStateException(buildOccupiedSeatMessage(occupiedSeats));
        }

        // 5. 주문 아이템 생성
        List<OrderItem> orderItems = OrderItem.createAll(concert, seatPricingPolicy, seats);

        // 6. 주문 생성 + 이벤트 발행
        Order order = Order.CreateOrder(user, orderItems);
        orderProducer.sendOrderEvent(OrderMapper.createOrderEvent(order));

        return orderRepository.save(order);
    }

    private String buildOccupiedSeatMessage(List<Seat> occupiedSeats) {
        return "이미 선점된 좌석입니다: " +
                occupiedSeats.stream()
                        .map(seat -> seat.getSection() + " " + seat.getRow() + "열 " + seat.getSeatNumber() + "번")
                        .collect(Collectors.joining(", "));
    }

}
