package com.hhp.concert.unitTest;

import com.hhp.concert.Business.service.SessionServiceImpl;
import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.Session;
import com.hhp.concert.Infrastructure.SessionRepositoryImpl;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SessionServiceTest {

    @Mock
    private SessionRepositoryImpl concertSessionRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;

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
        List<Session> sessionList = new ArrayList<>();
        for(long i = 1; i <= listSize; i++){
            sessionList.add(new Session(i, LocalDateTime.now().minusDays(i), concert));
        }

        given(concertSessionRepository.findAllByConcertId(concertId)).willReturn(sessionList);

        List<Session> list = sessionService.getSessionListByOpen(concertId);

        assertEquals(list.size(), listSize);
    }

    @Test
    @DisplayName("concertId session 유효 조회 테스트")
    public void getConcertIdBySessionTest(){
        Long concertId = 23L;
        Long sessionId = 124L;

        Concert concert = new Concert(concertId, "test");

        given(concertSessionRepository.findByIdAndConcertIdAndOpen(sessionId, concertId)).willReturn(Optional.of(new Session(sessionId, LocalDateTime.now(), concert)));

        Session response = sessionService.getSessionByOpenAndConcertId(sessionId, concertId);

        assertEquals(response.getId(), sessionId);
        assertEquals(response.getConcert().getId(), concertId);

    }

    @Test
    @DisplayName("concertId session 예외 테스트")
    public void getConcertIdBySessionConcertIdExceptionTest(){
        Long concertId = 23L;
        Long sessionId = 124L;

        Concert concert = new Concert(concertId, "test");

        given(concertSessionRepository.findByIdAndConcertIdAndOpen(sessionId, concertId)).willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            sessionService.getSessionByOpenAndConcertId(sessionId, concertId);
        });

        assertEquals(exception.getMsg(), ErrorCode.INVALID_SESSION_ID.getMsg());
    }

}
