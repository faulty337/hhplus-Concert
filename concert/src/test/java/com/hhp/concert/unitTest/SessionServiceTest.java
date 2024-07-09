package com.hhp.concert.unitTest;

import com.hhp.concert.Business.ConcertSessionServiceImpl;
import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Infrastructure.ConcertSessionRepositoryImpl;
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

public class SessionServiceTest {

    @Mock
    private ConcertSessionRepositoryImpl concertSessionRepository;

    @InjectMocks
    private ConcertSessionServiceImpl sessionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 객체 초기화
    }


    @Test
    @DisplayName("날짜 조회 성공 테스트")
    public void getDateListTest(){
        Long concertId = 1L;
        int listSize = 5;
        Concert concert = new Concert(concertId, "test");
        List<ConcertSession> sessionList = new ArrayList<>();
        for(long i = 1; i <= listSize; i++){
            sessionList.add(new ConcertSession(i, LocalDateTime.now().minusDays(i), concert));
        }

        given(concertSessionRepository.findAllByConcertId(concertId)).willReturn(sessionList);

        List<ConcertSession> list = sessionService.getSessionListByOpen(concertId);

        assertEquals(list.size(), listSize);
    }
}
