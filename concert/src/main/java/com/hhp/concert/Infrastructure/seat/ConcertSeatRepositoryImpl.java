package com.hhp.concert.Infrastructure.seat;

import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Repository.ConcertSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertSeatRepositoryImpl implements ConcertSeatRepository {
    private final ConcertSeatJpaRepository concertSeatJpaRepository;

    public List<ConcertSeat> findAllBySessionId(Long sessionId){
        return concertSeatJpaRepository.findAllByConcertSessionId(sessionId);
    }

    @Override
    public Optional<ConcertSeat> findByIdAndSessionId(Long seatId, Long sessionId) {
        return concertSeatJpaRepository.findByIdAndConcertSessionId(seatId, sessionId);
    }

    @Override
    public Optional<ConcertSeat> findById(Long concertSeatId) {
        return concertSeatJpaRepository.findById(concertSeatId);
    }

    @Override
    public ConcertSeat save(ConcertSeat concertSeat) {
        return concertSeatJpaRepository.save(concertSeat);
    }
}
