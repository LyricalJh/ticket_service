package com.example.concert.service.policy;

import com.example.concert.domain.concert.Concert;
import com.example.concert.domain.seat.Seat;

import java.math.BigDecimal;

public interface SeatPricingPolicy {

    BigDecimal calculatePrice(Concert concert, Seat seat);
}
