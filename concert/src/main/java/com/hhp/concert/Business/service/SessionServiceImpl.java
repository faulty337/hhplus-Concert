package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Session;
import com.hhp.concert.Business.Repository.SessionRepository;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    @Override
    public List<Session> getSessionListByOpen(Long concertId) {
        return sessionRepository.findAllByConcertId(concertId);
    }

    @Override
    public Session getSessionByOpenAndConcertId(Long concertId, Long sessionId) {
        return sessionRepository.findByIdAndConcertIdAndOpen(sessionId, concertId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_SESSION_ID)
        );
    }
}
