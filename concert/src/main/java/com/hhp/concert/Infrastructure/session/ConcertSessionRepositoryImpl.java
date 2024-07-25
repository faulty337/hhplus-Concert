package com.hhp.concert.Infrastructure.session;

import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.Repository.ConcertSessionRepository;
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
    public Optional<ConcertSession> findByIdAndConcertId(Long id, Long concertId) {
        return concertSessionJpaRepository.findByIdAndConcertId(id, concertId);
    }

    @Override
    public Optional<ConcertSession> findByIdAndConcertIdAndOpen(Long sessionId, Long concertId) {
        return concertSessionJpaRepository.findByIdAndConcertIdAndSessionTimeAfter(sessionId, concertId, LocalDateTime.now());
    }

    @Override
    public Optional<ConcertSession> findById(Long concertSessionId) {
        return concertSessionJpaRepository.findById(concertSessionId);
    }
}
