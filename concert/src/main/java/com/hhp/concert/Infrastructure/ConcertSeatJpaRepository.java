package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.ConcertSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeat, Long> {

    List<ConcertSeat> findAllByConcertSessionId(Long sessionId);
}
