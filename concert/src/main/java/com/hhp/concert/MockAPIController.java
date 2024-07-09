package com.hhp.concert;


import com.hhp.concert.Business.dto.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mock/concert")
public class MockAPIController {

    @GetMapping("/waiting")
    public GetWaitingTokenResponseDto getWaitingToken(){
        return new GetWaitingTokenResponseDto(999, false);
    }

    @GetMapping("/session/{concertId}")
    public List<GetSessionDateResponseDto> getSessionDate(@PathVariable String concertId){
        List<GetSessionDateResponseDto> response = new ArrayList<>();
        for(int i = 1; i < 24; i++){
            response.add(new GetSessionDateResponseDto(i, LocalDateTime.now()));
        }
        return response;
    }

    @GetMapping("/seat/{sessionId}")
    public GetSessionSeatResponseDto getSessionSeat(@PathVariable String sessionId){
        List<SeatInfoDto> seatList = new ArrayList<>();
        for(int i = 1; i <= 50; i++){
            seatList.add(new SeatInfoDto(i, false, 1000));
        }
        return new GetSessionSeatResponseDto(LocalDateTime.now(), seatList);
    }

    @PatchMapping("/charge")
    public ChargeResponseDto chargePoint() {
        return new ChargeResponseDto(1, 300);
    }

    @PostMapping("/reservation")
    public ReservationResponseDto reservation(
            @RequestBody ReservationRequestDto request
    ){
        return new ReservationResponseDto(3, 1000);
    }

    @PostMapping("/payment")
    public PaymentResponseDto payment(
            @RequestBody PaymentRequestDto request
    ){
        return new PaymentResponseDto(1, LocalDate.now(), 33);
    }
}
