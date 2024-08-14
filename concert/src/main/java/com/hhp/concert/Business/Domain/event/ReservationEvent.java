package com.hhp.concert.Business.Domain.event;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationEvent{
    long concertId;
    String concertTitle;
    long seatId;
    int seatNumber;
    @Setter
    long outboxId;

    public ReservationEvent(long concertId, String concertTitle, long seatId, int seatNumber) {
        this.concertId = concertId;
        this.concertTitle = concertTitle;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
    }

    @JsonCreator
    public ReservationEvent(@JsonProperty("concertId") Long concertId,
                            @JsonProperty("concertTitle") String concertTitle,
                            @JsonProperty("seatId") Long seatId,
                            @JsonProperty("seatNumber") int seatNumber) {
        this.concertId = concertId;
        this.concertTitle = concertTitle;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
    }
}
