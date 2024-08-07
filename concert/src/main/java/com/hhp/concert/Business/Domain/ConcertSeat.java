package com.hhp.concert.Business.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "concert_seat", indexes = {
        @Index(name = "idx_concert_session_id", columnList = "concertSessionId")
})
public class ConcertSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int seatNumber;

    private int price;

    private boolean available;

    private long concertSessionId;

    @Version
    private int version;

    public ConcertSeat(int seatNumber, int price, boolean available, Long concertSessionId) {
        this.seatNumber = seatNumber;
        this.price = price;
        this.available = available;
        this.concertSessionId = concertSessionId;
    }

    public ConcertSeat(Long id, int seatNumber, int price, boolean available, long concertSessionId) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.price = price;
        this.available = available;
        this.concertSessionId = concertSessionId;
    }

    public void reserveSeat(){
        this.available = false;
    }
}

