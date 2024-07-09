package com.hhp.concert.unitTest;

import com.hhp.concert.Business.ConcertSeatService;
import com.hhp.concert.Business.ConcertSeatServiceImpl;
import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Infrastructure.ConcertSeatRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SeatServiceTest {

    @Mock
    private ConcertSeatRepositoryImpl concertSeatRepository;

    @InjectMocks
    private ConcertSeatServiceImpl concertSeatService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 객체 초기화
    }


    @Test
    @DisplayName("sessionId에 해당하는 좌석 전체 조회")
    public void getSeatListTest(){
        Long sessionId = 1L;

        List<ConcertSeat> sessionList = new ArrayList<>();
        int listSize = 5;
        ConcertSession concertSession = new ConcertSession();

        for(int i = 1; i <= listSize; i++){
          sessionList.add(new ConcertSeat((long)i, i, 1000, false, concertSession));
        }

        given(concertSeatRepository.findAllByConcertSessionId(sessionId)).willReturn(sessionList);

        List<ConcertSeat> response = concertSeatService.getSessionBySeatList(sessionId);

        assertEquals(response.size(), listSize);
        assertFalse(response.get(0).isAvailable());


    }

}
