package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.Repository.ConcertRepository;
import com.hhp.concert.Business.Repository.ConcertSeatRepository;
import com.hhp.concert.Business.Repository.ConcertSessionRepository;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertServiceImpl implements ConcertService{
    private final ConcertRepository concertRepository;
    private final ConcertSessionRepository concertSessionRepository;
    private final ConcertSeatRepository concertSeatRepository;

    @Override
    @Transactional(readOnly = true)
    public Concert getConcert(Long concertId) {
        return concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CONCERT_ID)
        );
    }

    @Override
    public Concert getConcertBySessionId(Long concertSessionId) {
        ConcertSession concertSession = concertSessionRepository.findById(concertSessionId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND_SESSION_ID)
        );

        return concertRepository.findById(concertSession.getConcert().getId()).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND_SESSION_ID)
        );
    }

    @Override
    public ConcertSession getSession(Long concertSessionId) {
        return concertSessionRepository.findById(concertSessionId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND_SESSION_ID)
        );
    }

    @Override
    public ConcertSeat getAvailableReservationSeats(Long concertId, Long sessionId, Long seatId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CONCERT_ID)
        );
        ConcertSession concertSession = concertSessionRepository.findByIdAndConcertId(sessionId, concert.getId()).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_SESSION_ID)
        );
        ConcertSeat concertSeat= concertSeatRepository.findByIdAndSessionId(seatId, concertSession.getId()).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_SEAT_ID)
        );
        if(!concertSeat.isAvailable()){
            throw new CustomException(ErrorCode.NOT_AVAILABLE_SEAT);
        }
        return concertSeat;
    }

    @Override
    public ConcertSeat getSeat(Long concertId, Long sessionId, Long seatId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_CONCERT_ID)
        );
        ConcertSession concertSession = concertSessionRepository.findByIdAndConcertId(sessionId, concert.getId()).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_SESSION_ID)
        );
        return concertSeatRepository.findByIdAndSessionId(seatId, concertSession.getId()).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_SEAT_ID)
        );
    }
}
