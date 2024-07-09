package com.hhp.concert.Presentation;


import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import com.hhp.concert.Business.dto.GetSessionSeatResponseDto;
import com.hhp.concert.Business.dto.ReservationRequestDto;
import com.hhp.concert.Business.dto.ReservationResponseDto;
import com.hhp.concert.application.ConcertFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert")
public class ConcertController {

    private final ConcertFacade concertFacade;

    @GetMapping("/{concertId}/session")
    public List<GetSessionDateResponseDto> getSessionDate(
            @PathVariable Long concertId
    ){
        return concertFacade.getSessionDate(concertId);
    }

    @GetMapping("/{concertId}/seat")
    public GetSessionSeatResponseDto getSessionSeat(
            @PathVariable Long concertId,
            @RequestParam Long sessionId
    ){
        return concertFacade.getSessionSeat(concertId, sessionId);
    }


    @PostMapping("/{concertId}/reservation")
    public ReservationResponseDto reservationConcert(
            @PathVariable Long concertId,
            @RequestBody ReservationRequestDto requestDto
    ){
        return concertFacade.reservation(
                concertId,
                requestDto.getSessionId(),
                requestDto.getSeatId(),
                requestDto.getUserId(),
                requestDto.getToken()
        );
    }
}
