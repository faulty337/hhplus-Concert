package com.hhp.concert.Presentation;


import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
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
}
