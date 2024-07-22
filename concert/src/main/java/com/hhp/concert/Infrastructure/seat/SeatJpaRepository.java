package com.hhp.concert.Infrastructure.seat;

import com.hhp.concert.Business.Domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllBySessionId(Long sessionId);

    Optional<Seat> findByIdAndSessionId(Long seatId, Long sessionId);
}
