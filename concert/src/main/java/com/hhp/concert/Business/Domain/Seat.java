package com.hhp.concert.Business.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int seatNumber;

    private int price;

    private boolean available;

    @ManyToOne
    private Session session;

    public Seat(int seatNumber, int price, boolean available, Session session) {
        this.seatNumber = seatNumber;
        this.price = price;
        this.available = available;
        this.session = session;
    }
}

