package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.ConcertSession;

import java.util.List;
import java.util.Optional;

public interface ConcertSessionRepository {

    List<ConcertSession> findAllByConcertId(Long concertId);

    Optional<ConcertSession> findByIdAndConcertId(Long id, Long concertId);

    Optional<ConcertSession> findByIdAndConcertIdAndOpen(Long sessionId, Long concertId);


    Optional<ConcertSession> findById(Long concertSessionId);
}
