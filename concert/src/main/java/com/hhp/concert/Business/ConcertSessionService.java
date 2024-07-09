package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.ConcertSession;

import java.util.List;
import java.util.Optional;

public interface ConcertSessionService {
    public List<ConcertSession> getSessionListByOpen(Long concertId);

    ConcertSession getSessionByOpenAndConcertId(Long concertId, Long sessionId);
}
