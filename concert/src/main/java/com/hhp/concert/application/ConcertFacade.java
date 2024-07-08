package com.hhp.concert.application;

import com.hhp.concert.Business.ConcertService;
import com.hhp.concert.Business.ConcertSessionService;
import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {
    private final ConcertService concertService;
    private final ConcertSessionService concertSessionService;

    public List<GetSessionDateResponseDto> getSessionDate(Long concertId){
        Concert concert = concertService.getConcert(concertId);

        return concertSessionService.getSessionByOpen(concert.getId()).stream().map(session->new GetSessionDateResponseDto(session.getId(), session.getSessionTime())).toList();
    }

}
