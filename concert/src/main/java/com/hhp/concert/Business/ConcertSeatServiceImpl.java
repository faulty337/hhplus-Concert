package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Repository.ConcertSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertSeatServiceImpl implements ConcertSeatService{
    private final ConcertSeatRepository concertSeatRepository;


    @Override
    public List<ConcertSeat> getSessionBySeatList(Long sessionId) {
        return concertSeatRepository.findAllByConcertSessionId(sessionId);
    }
}
