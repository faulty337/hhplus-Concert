package com.hhp.concert;


import com.hhp.concert.Presentation.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.Presentation.dto.GetSessionDateResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
