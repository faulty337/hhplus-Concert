package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.ConcertSession;

import java.util.List;

public interface ConcertSessionService {
    public List<ConcertSession> getSessionListByOpen(Long concertId);
}
