package com.example.concert.service.policy;


import com.example.concert.domain.concert.Concert;
import com.example.concert.domain.seat.Seat;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DefaultSeatPricingPolicy implements SeatPricingPolicy {

    @Override
    public BigDecimal calculatePrice(Concert concert, Seat seat) {

        BigDecimal basePrice = concert.getBasePrice();

        BigDecimal gradeMultiplier = switch (seat.getGrade()) {
            case "VIP" -> BigDecimal.valueOf(3.0);
            case "R" -> BigDecimal.valueOf(2.0);
            case "S" -> BigDecimal.valueOf(1.5);
            default -> BigDecimal.ONE;
        };

        return basePrice.multiply(gradeMultiplier);
    }
}
