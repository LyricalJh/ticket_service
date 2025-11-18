package com.example.concert.domain.order;

import com.example.concert.service.dto.MyTicketDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        select new com.example.concert.service.dto.MyTicketDto(
            o.id,
            oi.id,
            c.concertId,
            c.title,
            c.date,
            c.venue,
            concat(s.section, ' ', s.row, '열 ', s.seatNumber, '번'),
            oi.price,
            o.status
        )
        from Order o
            join o.orderItems oi
            join oi.concert c
            join oi.seat s
        where o.user.id = :userId
        order by c.date desc, o.id desc, oi.id desc
        """)
    List<MyTicketDto> findMyTicketsByUserId(@Param("userId") Long userId);
}
