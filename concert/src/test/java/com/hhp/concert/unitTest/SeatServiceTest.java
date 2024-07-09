package com.hhp.concert.unitTest;

import com.hhp.concert.Business.service.SeatServiceImpl;
import com.hhp.concert.Business.Domain.Seat;
import com.hhp.concert.Business.Domain.Session;
import com.hhp.concert.Infrastructure.SeatRepositoryImpl;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
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
    private SeatRepositoryImpl seatRepository;

    @InjectMocks
    private SeatServiceImpl seatService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 객체 초기화
    }


    @Test
    @DisplayName("sessionId에 해당하는 좌석 전체 조회")
    public void getSeatListTest(){
        Long sessionId = 1L;

        List<Seat> sessionList = new ArrayList<>();
        int listSize = 5;
        Session concertSession = new Session();

        for(int i = 1; i <= listSize; i++){
          sessionList.add(new Seat((long)i, i, 1000, false, concertSession));
        }

        given(seatRepository.findAllBySessionId(sessionId)).willReturn(sessionList);

        List<Seat> response = seatService.getSessionBySeatList(sessionId);

        assertEquals(response.size(), listSize);
        assertFalse(response.get(0).isAvailable());


    }

}
