package com.hhp.concert.Presentation;


import com.hhp.concert.Business.WaitingService;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/concert")
public class WaitingController {

    private WaitingService waitingService;


    @GetMapping("/waiting")
    public GetWaitingTokenResponseDto getWaitingNumber(){

        return null;
    }
}
