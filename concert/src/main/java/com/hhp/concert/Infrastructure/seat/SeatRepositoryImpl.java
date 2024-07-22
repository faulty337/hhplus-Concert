package com.hhp.concert.Infrastructure.seat;

import com.hhp.concert.Business.Domain.Seat;
import com.hhp.concert.Business.Repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;

    public List<Seat> findAllBySessionId(Long sessionId){
        return seatJpaRepository.findAllBySessionId(sessionId);
    }

    @Override
    public Optional<Seat> findByIdAndSessionId(Long seatId, Long sessionId) {
        return seatJpaRepository.findByIdAndSessionId(seatId, sessionId);
    }
}
