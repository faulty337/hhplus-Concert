package com.hhp.concert.interfaces.controller;


import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import com.hhp.concert.Business.dto.GetSessionSeatResponseDto;
import com.hhp.concert.Business.dto.ReservationRequestDto;
import com.hhp.concert.Business.dto.ReservationResponseDto;
import com.hhp.concert.application.ConcertFacade;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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


    @PostMapping("/reservation")
    public ReservationResponseDto reserveConcert(
            @RequestBody ReservationRequestDto requestDto
    ){

        try{
            return concertFacade.reserveConcert(
                    requestDto.getConcertId(),
                    requestDto.getSessionId(),
                    requestDto.getSeatId(),
                    requestDto.getUserId()
            );
        }catch (ObjectOptimisticLockingFailureException e){
            throw new CustomException(ErrorCode.NOT_AVAILABLE_SEAT);
        }
    }
}
