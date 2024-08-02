package com.hhp.concert.Infrastructure.DBRepository.seat;

import com.hhp.concert.Business.Domain.ConcertSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeat, Long> {

    List<ConcertSeat> findAllByConcertSessionId(Long sessionId);

    Optional<ConcertSeat> findByIdAndConcertSessionId(Long seatId, Long sessionId);
}
