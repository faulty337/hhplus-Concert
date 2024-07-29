package com.hhp.concert.Business.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int amount;

    @ManyToOne
    private User user;

    @ManyToOne
    private Reservation reservation;

    public PaymentHistory(int amount, User user, Reservation reservation) {
        this.amount = amount;
        this.user = user;
        this.reservation = reservation;
    }
}
