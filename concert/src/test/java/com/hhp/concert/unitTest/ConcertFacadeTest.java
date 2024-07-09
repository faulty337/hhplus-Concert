package com.hhp.concert.unitTest;

import com.hhp.concert.Business.ConcertService;
import com.hhp.concert.Business.ConcertSessionService;
import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import com.hhp.concert.application.ConcertFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConcertFacadeTest {
    @InjectMocks
    private ConcertFacade concertFacade;

    @Mock
    private ConcertSessionService sessionService;

    @Mock
    private ConcertSeatService concertSeatService;

    @Mock
    private ConcertService concertService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 객체 초기화
    }


    @Test
    @DisplayName("예약 가능 날짜 조회")
    public void getSessionDateTest(){
        Long concertId = 1L;
        int listSize = 5;
        Concert concert = new Concert(concertId, "test");
        List<ConcertSession> sessionList = new ArrayList<>();
        for(long i = 1; i <= listSize; i++){
            sessionList.add(new ConcertSession(i, LocalDateTime.now().minusDays(i), concert));
        }
        given(concertService.getConcert(concertId)).willReturn(concert);
        given(sessionService.getSessionListByOpen(concertId)).willReturn(sessionList);

        List<GetSessionDateResponseDto> response = concertFacade.getSessionDate(concertId);

        assertEquals(response.size(), listSize);

    }
}
