package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.ConcertSession;

import java.util.List;

public interface ConcertSessionService {
    public List<ConcertSession> getSessionListByOpen(Long concertId);

    ConcertSession getSessionByOpenAndConcertId(Long concertId, Long sessionId);
}
