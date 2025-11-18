package com.example.concert.service;

import com.example.concert.domain.order.OrderRepository;
import com.example.concert.service.dto.MyTicketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketQueryService {

    private final OrderRepository orderRepository;

    public List<MyTicketDto> getMyTickets(Long userId) {
        return orderRepository.findMyTicketsByUserId(userId);
    }
}
