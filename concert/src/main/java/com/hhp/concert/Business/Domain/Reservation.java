package com.hhp.concert.Business.Domain;

import com.hhp.concert.Business.Domain.enums.ReservationStatus;
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

}
