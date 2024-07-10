package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {

    List<Session> findAllByConcertId(Long concertId);

    Optional<Session> findByIdAndConcertIdAndOpen(Long sessionId, Long concertId);


}
