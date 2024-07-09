package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.ConcertSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertSessionJpaRepository extends JpaRepository<ConcertSession, Long> {
    List<ConcertSession> findAllByConcertIdAndSessionTimeAfter(Long concertId, LocalDateTime now);

    Optional<ConcertSession> findByIdAndConcertIdAndSessionTimeBefore(Long sessionId, Long concertId, LocalDateTime now);

}
