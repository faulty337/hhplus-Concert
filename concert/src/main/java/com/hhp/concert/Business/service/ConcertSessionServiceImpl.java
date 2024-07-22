package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.Repository.ConcertSessionRepository;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertSessionServiceImpl implements ConcertSessionService {
    private final ConcertSessionRepository concertSessionRepository;

    @Override
    public List<ConcertSession> getSessionListByOpen(Long concertId) {
        return concertSessionRepository.findAllByConcertId(concertId);
    }

    @Override
    public ConcertSession getSessionByOpenAndConcertId(Long concertId, Long sessionId) {
        return concertSessionRepository.findByIdAndConcertIdAndOpen(sessionId, concertId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_SESSION_ID)
        );
    }
}
