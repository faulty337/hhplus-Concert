package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    List<Seat> findAllBySessionId(Long sessionId);

    Optional<Seat> findByIdAndSessionId(Long seatId, Long sessionId);
}
