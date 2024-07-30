package com.hhp.concert.unitTest;

import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.service.ConcertSeatServiceImpl;
import com.hhp.concert.Infrastructure.seat.ConcertSeatRepositoryImpl;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConcertSeatServiceTest {

    @Mock
    private ConcertSeatRepositoryImpl seatRepository;

    @InjectMocks
    private ConcertSeatServiceImpl seatService;

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
          sessionList.add(new ConcertSeat((long)i, i, 1000, false, sessionId));
        }

        given(seatRepository.findAllBySessionId(sessionId)).willReturn(sessionList);

        List<ConcertSeat> response = seatService.getSessionBySeatList(sessionId);

        assertEquals(response.size(), listSize);
        assertFalse(response.get(0).isAvailable());
    }

    @Test
    @DisplayName("좌석 정보 반환")
    public void getSeatTest(){
        Long sessionId = 24L;
        Long seatId = 12414L;
        int seatNumber = 32;

        given(seatRepository.findByIdAndSessionId(seatId, sessionId)).willReturn(Optional.of(new ConcertSeat(seatId, seatNumber, 1000, true, sessionId)));

        ConcertSeat concertSeat = seatService.getSeatsForConcertSessionAndAvailable(sessionId, seatId);
        assertNotNull(concertSeat);
        assertEquals(seatId, concertSeat.getId());
        assertEquals(seatNumber, concertSeat.getSeatNumber());
        assertTrue(concertSeat.isAvailable());

    }

    @Test
    @DisplayName("좌석 정보 반환 seatId 예외 테스트")
    public void getSeatIdExceptionTest(){
        Long sessionId = 24L;
        Long seatId = 12414L;
        int seatNumber = 32;

        given(seatRepository.findByIdAndSessionId(seatId, sessionId)).willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            seatService.getSeatsForConcertSessionAndAvailable(sessionId, seatId);
        });

        assertEquals(exception.getMsg(), ErrorCode.NOT_FOUND_SEAT_NUMBER.getMsg());

    }

    @Test
    @DisplayName("좌석 정보 반환 seatId 예외 테스트")
    public void getSeatNotAvailableExceptionTest(){
        Long sessionId = 24L;
        Long seatId = 12414L;
        int seatNumber = 32;

        given(seatRepository.findByIdAndSessionId(seatId, sessionId)).willReturn(Optional.of(new ConcertSeat(seatId, seatNumber, 1000, false, sessionId)));

        CustomException exception = assertThrows(CustomException.class, () -> {
            seatService.getSeatsForConcertSessionAndAvailable(sessionId, seatId);
        });

        assertEquals(exception.getMsg(), ErrorCode.NOT_AVAILABLE_SEAT.getMsg());

    }


}
