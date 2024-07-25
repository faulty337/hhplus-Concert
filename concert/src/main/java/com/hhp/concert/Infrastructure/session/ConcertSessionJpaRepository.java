package com.hhp.concert.Infrastructure.session;

import com.hhp.concert.Business.Domain.ConcertSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertSessionJpaRepository extends JpaRepository<ConcertSession, Long> {
    List<ConcertSession> findAllByConcertIdAndSessionTimeAfter(Long concertId, LocalDateTime now);

    Optional<ConcertSession> findByIdAndConcertIdAndSessionTimeAfter(Long sessionId, Long concertId, LocalDateTime now);

    Optional<ConcertSession> findByIdAndConcertId(Long id, Long concertId);
}
