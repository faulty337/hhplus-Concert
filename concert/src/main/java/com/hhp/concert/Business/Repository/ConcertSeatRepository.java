package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.ConcertSeat;

import java.util.List;
import java.util.Optional;

public interface ConcertSeatRepository {
    List<ConcertSeat> findAllBySessionId(Long sessionId);

    Optional<ConcertSeat> findByIdAndSessionId(Long seatId, Long sessionId);
}
