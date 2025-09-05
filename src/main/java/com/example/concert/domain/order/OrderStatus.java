package com.example.concert.domain.order;

public enum OrderStatus {
    CREATED, // 생성됨
    PAID,    // 결제 완료
    FAILED,  // 결제 실패
    CANCELLED // 취소
}