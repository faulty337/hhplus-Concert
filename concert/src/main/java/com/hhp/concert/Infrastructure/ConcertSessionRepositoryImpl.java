package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Repository.ConcertSessionRepository;
import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertSessionRepositoryImpl implements ConcertSessionRepository {
    private final ConcertSessionJpaRepository concertSessionJpaRepository;

    @Override
    public List<ConcertSession> findAllByConcertId(Long concertId) {
        return concertSessionJpaRepository.findAllByConcertIdAndSessionTimeAfter(concertId, LocalDateTime.now());
    }
}
