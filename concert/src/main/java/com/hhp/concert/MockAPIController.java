package com.hhp.concert;


import com.hhp.concert.Presentation.dto.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mock/concert")
public class MockAPIController {

    @GetMapping("/waiting")
    public GetWaitingTokenResponseDto getWaitingToken(){
        return new GetWaitingTokenResponseDto(999, "token", false);
    }

    @GetMapping("/session/{concertId}")
    public List<GetSessionDateResponseDto> getSessionDate(@PathVariable String concertId){
        List<GetSessionDateResponseDto> response = new ArrayList<>();
        for(int i = 1; i < 24; i++){
            response.add(new GetSessionDateResponseDto(i, LocalDate.now(), 23));
        }
        return response;
    }

    @GetMapping("/seat/{sessionId}")
    public GetSessionSeatResponseDto getSessionSeat(@PathVariable String sessionId){
        List<SeatInfoDto> seatList = new ArrayList<>();
        for(int i = 1; i <= 50; i++){
            seatList.add(new SeatInfoDto(i, false));
        }
        return new GetSessionSeatResponseDto(LocalDate.now(), seatList);
    }

    @PatchMapping("/charge")
    public GetChargeResponseDto chargePoint() {
        return new GetChargeResponseDto(1, 300);
    }

    @PostMapping("/reservation")
    public GetReservationResponseDto reservation(
            @RequestBody GetReservationRequestDto request
    ){
        return new GetReservationResponseDto(3);
    }

    
}
