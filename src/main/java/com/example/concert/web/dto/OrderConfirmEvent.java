package com.example.concert.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmEvent {

    private Long orderId;
    private String status;
    private String userEmail;
    private String message;

}