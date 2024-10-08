package com.hhp.concert.unitTest;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.Domain.event.ReservationEvent;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import com.hhp.concert.Business.dto.GetSessionSeatResponseDto;
import com.hhp.concert.Business.dto.ReservationResponseDto;
import com.hhp.concert.application.ConcertFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConcertFacadeTest {

    private static final Logger logger = LogManager.getLogger(ConcertFacadeTest.class);

    @InjectMocks
    private ConcertFacade concertFacade;

    @Mock
    private ConcertSessionService concertSessionService;

    @Mock
    private ConcertSeatService concertSeatService;

    @Mock
    private ConcertService concertService;

    @Mock
    private UserService userService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;



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
        List<ConcertSession> concertSessionList = new ArrayList<>();
        for(long i = 1; i <= listSize; i++){
            concertSessionList.add(new ConcertSession(i, LocalDateTime.now().minusDays(i), concertId));
        }
        given(concertService.getConcert(concertId)).willReturn(concert);
        given(concertSessionService.getSessionListByOpen(concertId)).willReturn(concertSessionList);


        List<GetSessionDateResponseDto> response = concertFacade.getSessionDate(concertId);

        assertEquals(response.size(), listSize);

    }

    @Test
    @DisplayName("좌석 조회 테스트")
    public void getSeatListTest(){
        Long concertId = 1L;
        Long sessionId = 1L;
        int listSize = 5;
        List<ConcertSeat> concertSeatList = new ArrayList<>();
        Concert concert = new Concert(concertId, "test");
        ConcertSession concertSession = new ConcertSession(sessionId, LocalDateTime.now(), concertId);
        for(int i = 1; i<= 5; i++){
            concertSeatList.add(new ConcertSeat((long)i, i, 1000, false, concertSession.getId()));
        }


        given(concertService.getConcert(concertId)).willReturn(concert);
        given(concertSessionService.getSessionByOpenAndConcertId(concertId, sessionId)).willReturn(concertSession);
        given(concertSeatService.getSessionBySeatList(sessionId)).willReturn(concertSeatList);

        GetSessionSeatResponseDto response = concertFacade.getSessionSeat(concertId, sessionId);

        assertEquals(response.getDate(), concertSession.getSessionTime());
        assertEquals(response.getSeatList().size(), listSize);

    }

    @Test
    @DisplayName("예약 테스트")
    public void getReserveConcertTest(){
        long userId = 1L;
        Long concertId = 1L;
        Long sessionId = 1L;
        Long seatId = 2L;
        Long reservationId = 23L;
        User user = new User(userId, null, 1000);
        Concert concert = new Concert(concertId, "test");
        ConcertSession concertSession = new ConcertSession(sessionId, LocalDateTime.now(), concertId);
        ConcertSeat concertSeat = new ConcertSeat(seatId, 1, 1000, false, concertSession.getId());
        Reservation reservation = new Reservation(reservationId, concertId, user.getId(), concertSession.getId(), concertSeat.getId(), concertSeat.getPrice(), Reservation.ReservationStatus.PENDING);
        ReservationEvent reservationEvent = new ReservationEvent(concertId, "", seatId, 1);
        given(concertService.getConcert(concertId)).willReturn(concert);
        given(concertSessionService.getSessionByOpenAndConcertId(concertId, sessionId)).willReturn(concertSession);
        given(concertSeatService.getSeatsForConcertSessionAndAvailable(sessionId, seatId)).willReturn(concertSeat);
        given(userService.getUser(userId)).willReturn(user);
        given(reservationService.createReservation(any(Reservation.class))).willReturn(reservation);
        given(concertService.getAvailableReservationSeats(concertId, sessionId, seatId)).willReturn(concertSeat);
        given(reservationService.createEvent(reservationId)).willReturn(reservationEvent);
//        given(applicationEventPublisher.publishEvent(reservationEvent));
        ReservationResponseDto response = concertFacade.reserveConcert(concertId, sessionId, seatId, userId);

        assertEquals(response.getReservationId(), reservation.getId());
        assertEquals(response.getPrice(), concertSeat.getPrice());

    }





}
