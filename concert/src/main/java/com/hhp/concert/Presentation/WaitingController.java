package com.hhp.concert.Presentation;


import com.hhp.concert.Business.dto.GetTokenResponseDto;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.application.WaitingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/concert")
@RequiredArgsConstructor
public class WaitingController {

    private final WaitingFacade waitingFacade;


    @GetMapping("/waiting/status")
    public GetWaitingTokenResponseDto getWaitingNumber(
            @RequestParam String token
    ){
        return waitingFacade.getWaitingInfo(token);
    }

    @GetMapping("/waiting/token")
    public GetTokenResponseDto getWaitingToken(
            @RequestParam long userId
    ){
        return waitingFacade.getToken(userId);
    }
}
