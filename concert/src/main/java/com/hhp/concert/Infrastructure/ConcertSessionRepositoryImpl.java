package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Repository.ConcertSessionRepository;
import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertSessionRepositoryImpl implements ConcertSessionRepository {
    private final ConcertSessionJpaRepository concertSessionJpaRepository;

    @Override
    public List<ConcertSession> findAllByConcertId(Long concertId) {
        return concertSessionJpaRepository.findAllByConcertIdAndSessionTimeAfter(concertId, LocalDateTime.now());
    }

    @Override
    public Optional<ConcertSession> findByIdAndConcertIdAndOpen(Long sessionId, Long concertId) {
        return concertSessionJpaRepository.findByIdAndConcertIdAndSessionTimeBefore(sessionId, concertId, LocalDateTime.now());
    }
}
