package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionJpaRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByConcertIdAndSessionTimeAfter(Long concertId, LocalDateTime now);

    Optional<Session> findByIdAndConcertIdAndSessionTimeAfter(Long sessionId, Long concertId, LocalDateTime now);

}
