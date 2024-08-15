package com.hhp.concert.Business.Domain.event;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationEvent{
    private UUID eventId;
    private long concertId;
    private String concertTitle;
    private long seatId;
    private int seatNumber;

    public ReservationEvent(long concertId, String concertTitle, long seatId, int seatNumber) {
        this.concertId = concertId;
        this.concertTitle = concertTitle;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.eventId = UUID.randomUUID();
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
        this.eventId = UUID.randomUUID();
    }
}
