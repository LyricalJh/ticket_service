package com.example.concert.domain.seat;

import com.example.concert.domain.concert.Concert;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA 기본 생성자 (필수)
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더와 함께 쓰기 좋음
@Builder
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    private String section;

    private String row;

    @Column(name = "seat_number")
    private Integer seatNumber;

    // 등급별로 가격 변경
    private String grade;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    public void changeStatus(SeatStatus status) {
        this.status = status;
    }
}
