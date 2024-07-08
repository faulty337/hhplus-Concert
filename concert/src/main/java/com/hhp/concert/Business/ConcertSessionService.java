package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSession;

import java.util.List;

public interface ConcertSessionService {
    public List<ConcertSession> getSessionByOpen(Long concertId);
}
