package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.Repository.ConcertSessionRepository;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConcertSessionServiceImpl implements ConcertSessionService{
    private final ConcertSessionRepository concertSessionRepository;

    @Override
    public List<ConcertSession> getSessionListByOpen(Long concertId) {
        return concertSessionRepository.findAllByConcertId(concertId);
    }

    @Override
    public ConcertSession getSessionByOpenAndConcertId(Long concertId, Long sessionId) {
        return concertSessionRepository.findByIdAndConcertIdAndOpen(sessionId, concertId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_SESSION_ID)
        );
    }
}
