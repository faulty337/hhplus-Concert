package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.Repository.ConcertSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertSessionServiceImpl implements ConcertSessionService{
    private final ConcertSessionRepository concertSessionRepository;

    @Override
    public List<ConcertSession> getSessionListByOpen(Long concertId) {
        return concertSessionRepository.findAllByConcertId(concertId);
    }
}
