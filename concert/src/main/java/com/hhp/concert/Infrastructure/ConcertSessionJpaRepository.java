package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.ConcertSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertSessionJpaRepository extends JpaRepository<ConcertSession, Long> {
    List<ConcertSession> findAllByConcertIdAndSessionTimeAfter(Long concertId, LocalDateTime now);
}
