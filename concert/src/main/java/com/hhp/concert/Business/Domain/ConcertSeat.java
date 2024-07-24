package com.hhp.concert.Business.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConcertSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int seatNumber;

    private int price;

    private boolean available;

    @ManyToOne
    private ConcertSession concertSession;

    public ConcertSeat(int seatNumber, int price, boolean available, ConcertSession concertSession) {
        this.seatNumber = seatNumber;
        this.price = price;
        this.available = available;
        this.concertSession = concertSession;
    }

    public void reserveSeat(){
        this.available = false;
    }
}

