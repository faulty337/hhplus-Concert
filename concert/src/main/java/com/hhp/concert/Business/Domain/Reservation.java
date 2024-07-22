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
public class Reservation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Session session;

    @ManyToOne
    private Seat seat;

    private int reservationPrice;

    @Setter
    private ReservationStatus status = ReservationStatus.PENDING;


    public Reservation(User user, Session session, Seat seat, int reservationPrice) {
        this.user = user;
        this.session = session;
        this.seat = seat;
        this.reservationPrice = reservationPrice;
    }

    public enum ReservationStatus {
        PENDING,        // 예약 대기 중
        CONFIRMED,      // 예약 확정
        CANCELLED,      // 예약 취소됨
        COMPLETED       // 예약 완료됨 (사용자에 의해 사용됨)
    }
}
