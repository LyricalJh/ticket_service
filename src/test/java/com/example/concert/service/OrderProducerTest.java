package com.example.concert.service;

import com.example.concert.batch.QueueScheduler;
import com.example.concert.domain.concert.*;
import com.example.concert.domain.order.Order;
import com.example.concert.domain.order.OrderItem;
import com.example.concert.domain.order.OrderItemRepository;
import com.example.concert.domain.order.OrderRepository;
import com.example.concert.domain.seat.Seat;
import com.example.concert.domain.seat.SeatRepository;
import com.example.concert.domain.user.User;
import com.example.concert.domain.user.UserRepository;
import com.example.concert.domain.user.UserRole;
import com.example.concert.producer.OrderProducer;
import com.example.concert.queue.EnterQueueService;
import com.example.concert.service.policy.DefaultSeatPricingPolicy;
import com.example.concert.web.mapper.OrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderProducerTest {

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EnterQueueService enterQueueService;

    @Autowired
    QueueScheduler scheduler;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("좌석을 예매하고 주문을 합니다.")
    public void reservationTest() throws Exception {
        // given
        User user = User.builder()
                .email("test@email.com")
                .phoneNumber("123456789")
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        User targetUser = userRepository.findByEmail("test@email.com").orElseThrow();

        Concert concert = Concert.builder()
                .title("Coldplay World Tour 2025")
                .date(LocalDateTime.of(2025, 12, 1, 19, 0))
                .venue("서울 잠실 주경기장")
                .basePrice(BigDecimal.valueOf(100_000)) // 기본가 10만원
                .status(ConcertStatus.OPEN)
                .openAt(LocalDateTime.now().minusDays(1))
                .closeAt(LocalDateTime.now().plusDays(30))
                .updateAt(LocalDateTime.now())
                .createAt(LocalDateTime.now().minusDays(10))
                .build();

        Concert savedConcert = concertRepository.save(concert);
        Concert targetConcert = concertRepository.findById(savedConcert.getConcertId()).orElseThrow();

        Seat seat1 = Seat.builder()
                .section("A구역")
                .row("A열")
                .seatNumber(1)
                .grade("VIP")
                .concert(targetConcert)
                .build();

        Seat seat2 = Seat.builder()
                .section("A구역")
                .row("A열")
                .seatNumber(2)
                .grade("R")
                .concert(targetConcert)
                .build();

        Seat seat3 = Seat.builder()
                .section("A구역")
                .row("A열")
                .seatNumber(3)
                .grade("S")
                .concert(targetConcert)
                .build();

        List<Seat> seats = seatRepository.saveAll(Arrays.asList(seat1, seat2, seat3));
        List<Long> seatIds = seats.stream().map(Seat::getId).toList();

        enterQueueService.enterQueue(targetConcert.getConcertId(), user.getId());

        // when
        scheduler.process();
        Order order = orderService.createOrder(targetUser, targetConcert.getConcertId(), seatIds);
        List<OrderItem> orderItems = OrderItem.createAll(order, concert, new DefaultSeatPricingPolicy(), seats);
        orderItemRepository.saveAll(orderItems);

        // then
        System.out.println("총 주문 금액: " + order.getTotalAmount());
        assertNotNull(order);
        assertEquals(3, order.getOrderItems().size());
        assertThat(order.getTotalAmount().compareTo(BigDecimal.valueOf(650_000))).isZero();
        orderProducer.send(OrderMapper.createOrderEvent(order));
    }

}