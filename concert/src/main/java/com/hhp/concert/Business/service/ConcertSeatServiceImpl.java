package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Repository.ConcertSeatRepository;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertSeatServiceImpl implements ConcertSeatService {
    private final ConcertSeatRepository concertSeatRepository;


    @Override
    public List<ConcertSeat> getSessionBySeatList(Long sessionId) {
        return concertSeatRepository.findAllBySessionId(sessionId);
    }

    @Override
    public ConcertSeat getSeatsForConcertSessionAndAvailable(Long sessionId, Long seatId) {
        ConcertSeat concertSeat = concertSeatRepository.findByIdAndSessionId(seatId, sessionId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_SEAT_NUMBER)
        );
        if(!concertSeat.isAvailable()){
            throw new CustomException(ErrorCode.NOT_AVAILABLE_SEAT);
        }
        return concertSeat;
    }



}
