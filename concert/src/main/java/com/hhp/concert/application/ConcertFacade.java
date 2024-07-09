package com.hhp.concert.application;

import com.hhp.concert.Business.ConcertSeatService;
import com.hhp.concert.Business.ConcertService;
import com.hhp.concert.Business.ConcertSessionService;
import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import com.hhp.concert.Business.dto.GetSessionSeatResponseDto;
import com.hhp.concert.Business.dto.SeatInfoDto;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {
    private final ConcertService concertService;
    private final ConcertSessionService concertSessionService;
    private final ConcertSeatService concertSeatService;


    public List<GetSessionDateResponseDto> getSessionDate(Long concertId){
        Concert concert = concertService.getConcert(concertId);

        return concertSessionService.getSessionListByOpen(concert.getId()).stream().map(session->new GetSessionDateResponseDto(session.getId(), session.getSessionTime())).toList();
    }

    public GetSessionSeatResponseDto getSessionSeat(Long concertId, Long sessionId) {
        Concert concert = concertService.getConcert(concertId);

        ConcertSession concertSession = concertSessionService.getSessionByOpenAndConcertId(sessionId, concert.getId());

        List<ConcertSeat> concertSeatList = concertSeatService.getSessionBySeatList(concertSession.getId());

        return new GetSessionSeatResponseDto(concertSession.getSessionTime(), concertSeatList.stream().map(seat -> new SeatInfoDto(seat.getSeatNumber(), seat.isAvailable(), seat.getPrice())).toList());
    }
}
