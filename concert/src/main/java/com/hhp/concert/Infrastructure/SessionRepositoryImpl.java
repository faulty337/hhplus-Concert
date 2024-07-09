package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Repository.SessionRepository;
import com.hhp.concert.Business.Domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {
    private final SessionJpaRepository sessionJpaRepository;

    @Override
    public List<Session> findAllByConcertId(Long concertId) {
        return sessionJpaRepository.findAllByConcertIdAndSessionTimeAfter(concertId, LocalDateTime.now());
    }

    @Override
    public Optional<Session> findByIdAndConcertIdAndOpen(Long sessionId, Long concertId) {
        return sessionJpaRepository.findByIdAndConcertIdAndSessionTimeBefore(sessionId, concertId, LocalDateTime.now());
    }
}
