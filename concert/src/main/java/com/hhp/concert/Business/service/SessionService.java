package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Session;

import java.util.List;

public interface SessionService {
    public List<Session> getSessionListByOpen(Long concertId);

    Session getSessionByOpenAndConcertId(Long concertId, Long sessionId);
}
