package com.hhp.concert.unitTest;

import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.service.ConcertSessionServiceImpl;
import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Infrastructure.session.ConcertSessionRepositoryImpl;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
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

public class ConcertSessionServiceTest {

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
        List<ConcertSession> concertSessionList = new ArrayList<>();
        for(long i = 1; i <= listSize; i++){
            concertSessionList.add(new ConcertSession(i, LocalDateTime.now().minusDays(i), concertId));
        }

        given(concertSessionRepository.findAllByConcertId(concertId)).willReturn(concertSessionList);

        List<ConcertSession> list = sessionService.getSessionListByOpen(concertId);

        assertEquals(list.size(), listSize);
    }

    @Test
    @DisplayName("concertId session 유효 조회 테스트")
    public void getConcertIdBySessionTest(){
        Long concertId = 23L;
        Long sessionId = 124L;

        Concert concert = new Concert(concertId, "test");

        given(concertSessionRepository.findByIdAndConcertIdAndOpen(sessionId, concertId)).willReturn(Optional.of(new ConcertSession(sessionId, LocalDateTime.now().plusDays(1), concertId)));


        ConcertSession response = sessionService.getSessionByOpenAndConcertId(concertId, sessionId);

        assertEquals(response.getId(), sessionId);
        assertEquals(response.getConcertId(), concertId);

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

        assertEquals(exception.getMsg(), ErrorCode.NOT_FOUND_SESSION_ID.getMsg());
    }

}
