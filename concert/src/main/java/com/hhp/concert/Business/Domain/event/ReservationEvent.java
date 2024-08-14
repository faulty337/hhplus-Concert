package com.hhp.concert.Business.Domain.event;


public record ReservationEvent(long concertId,
                               String concertTitle,
                               long seatId,
                               int seatNumber,
                               long outboxId
                               ) {}
