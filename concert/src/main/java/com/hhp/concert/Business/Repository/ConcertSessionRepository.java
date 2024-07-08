package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSession;

import java.util.List;

public interface ConcertSessionRepository {

    List<ConcertSession> findAllByConcertId(Long concertId);
}
