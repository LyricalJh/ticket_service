package com.example.concert.domain.concert;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA 기본 생성자 (필수)
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더와 함께 쓰기 좋음
@Builder
@Table(name = "concerts")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long concertId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "concert_id")
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();

    @Column(name = "title")
    private String title;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "venue")
    private String venue;

    @Column(name = "base_price", scale = 2)
    private BigDecimal basePrice;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private ConcertStatus status;

    @Column(name = "open_at")
    private LocalDateTime openAt;

    @Column(name = "close_at")
    private LocalDateTime closeAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "create_at")
    private LocalDateTime createAt;

}
