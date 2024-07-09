package com.hhp.concert.Presentation;


import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import com.hhp.concert.application.ConcertFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertFacade concertFacade;

    @GetMapping("/concert/{concertId}/session")
    public List<GetSessionDateResponseDto> getSessionDate(
            @PathVariable Long concertId
    ){
        return concertFacade.getSessionDate(concertId);
    }
}
