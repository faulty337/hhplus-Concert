package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.ConcertSeat;

import java.util.List;

public interface ConcertSeatService {

    List<ConcertSeat> getSessionBySeatList(Long sessionId);

    ConcertSeat getSeatsForConcertSessionAndAvailable(Long sessionId, Long seatId);
}
