package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Seat;
import com.hhp.concert.Business.Repository.SeatRepository;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;


    @Override
    public List<Seat> getSessionBySeatList(Long sessionId) {
        return seatRepository.findAllBySessionId(sessionId);
    }

    @Override
    public Seat getSeatsForConcertSessionAndAvailable(Long sessionId, Long seatId) {
        Seat seat = seatRepository.findByIdAndSessionId(seatId, sessionId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_SEAT_NUMBER)
        );
        if(!seat.isAvailable()){
            throw new CustomException(ErrorCode.NOT_AVAILABLE_SEAT);
        }
        return seat;
    }



}
