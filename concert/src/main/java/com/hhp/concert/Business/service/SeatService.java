package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Seat;

import java.util.List;

public interface SeatService {

    List<Seat> getSessionBySeatList(Long sessionId);

    Seat getSeatsForConcertSessionAndAvailable(Long sessionId, Long seatId);
}
