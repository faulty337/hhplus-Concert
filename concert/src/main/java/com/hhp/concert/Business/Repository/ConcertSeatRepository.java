package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.ConcertSeat;

import java.util.List;

public interface ConcertSeatRepository {
    List<ConcertSeat> findAllByConcertSessionId(Long sessionId);
}
