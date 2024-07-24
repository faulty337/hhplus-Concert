package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.util.exception.CustomException;

public interface ConcertService {
    public Concert getConcert(Long concertId);

    ConcertSeat getSeat(Long concertId, Long sessionId, Long seatId);

    Concert getConcertBySessionId(Long concertSessionId);

    ConcertSession getSession(Long concertSessionId);

    ConcertSeat getAvailableReservationSeats(Long concertId, Long sessionId, Long seatId);
}
