package com.hhp.concert.Business.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private Long concertSessionId;

    private Long concertSeatId;

    private int reservationPrice;

    @Setter
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Version
    private long version;

    public Reservation(Long id, Long userId, Long concertSessionId, Long concertSeatId, int reservationPrice, ReservationStatus status) {
        this.id = id;
        this.userId = userId;
        this.concertSessionId = concertSessionId;
        this.concertSeatId = concertSeatId;
        this.reservationPrice = reservationPrice;
        this.status = status;
    }

    public Reservation(Long userId, Long concertSessionId, Long concertSeatId, int price) {
        this.userId = userId;
        this.concertSessionId = concertSessionId;
        this.concertSeatId = concertSeatId;
        this.reservationPrice = price;
    }

    public enum ReservationStatus {
        PENDING,        // 예약 대기 중
        CONFIRMED,      // 예약 확정
        CANCELLED,      // 예약 취소됨
        COMPLETED       // 예약 완료됨 (사용자에 의해 사용됨)
    }
}
