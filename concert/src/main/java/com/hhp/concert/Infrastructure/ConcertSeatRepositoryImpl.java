package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Repository.ConcertSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertSeatRepositoryImpl implements ConcertSeatRepository {
    private final ConcertSeatJpaRepository concertSeatJpaRepository;

    public List<ConcertSeat> findAllByConcertSessionId(Long sessionId){
        return concertSeatJpaRepository.findAllByConcertSessionId(sessionId);
    }
}
