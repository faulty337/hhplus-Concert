package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.ConcertSeat;

import java.util.List;

public interface ConcertSeatService{

    List<ConcertSeat> getSessionBySeatList(Long sessionId);
}
