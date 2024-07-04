package com.hhp.concert;


import com.hhp.concert.Presentation.dto.GetWaitingTokenResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/mock/concert")
public class MockAPIController {

    @GetMapping("/waiting")
    public GetWaitingTokenResponseDto getWaitingToken(){
        return new GetWaitingTokenResponseDto(999, "token", false);
    }



}
