package com.hhp.concert.unitTest;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.Domain.enums.ReservationStatus;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import com.hhp.concert.Business.dto.GetSessionSeatResponseDto;
import com.hhp.concert.Business.dto.ReservationResponseDto;
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
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConcertFacadeTest {
    @InjectMocks
    private ConcertFacade concertFacade;

    @Mock
    private SessionService sessionService;

    @Mock
    private SeatService seatService;

    @Mock
    private ConcertService concertService;

    @Mock
    private UserService userService;

    @Mock
    private ReservationService reservationService;

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
        List<Session> sessionList = new ArrayList<>();
        for(long i = 1; i <= listSize; i++){
            sessionList.add(new Session(i, LocalDateTime.now().minusDays(i), concert));
        }
        given(concertService.getConcert(concertId)).willReturn(concert);
        given(sessionService.getSessionListByOpen(concertId)).willReturn(sessionList);

        List<GetSessionDateResponseDto> response = concertFacade.getSessionDate(concertId);

        assertEquals(response.size(), listSize);

    }

    @Test
    @DisplayName("좌석 조회 테스트")
    public void getSeatListTest(){
        Long concertId = 1L;
        Long sessionId = 1L;
        int listSize = 5;
        List<Seat> seatList = new ArrayList<>();
        Concert concert = new Concert(concertId, "test");
        Session session = new Session(sessionId, LocalDateTime.now(), concert);
        for(int i = 1; i<= 5; i++){
            seatList.add(new Seat((long)i, i, 1000, false, session));
        }


        given(concertService.getConcert(concertId)).willReturn(concert);
        given(sessionService.getSessionByOpenAndConcertId(concertId, sessionId)).willReturn(session);
        given(seatService.getSessionBySeatList(sessionId)).willReturn(seatList);

        GetSessionSeatResponseDto response = concertFacade.getSessionSeat(concertId, sessionId);

        assertEquals(response.getDate(), session.getSessionTime());
        assertEquals(response.getSeatList().size(), listSize);

    }

    @Test
    @DisplayName("예약 테스트")
    public void getReservationTest(){
        long userId = 1L;
        Long concertId = 1L;
        Long sessionId = 1L;
        Long seatId = 2L;
        Long reservationId = 23L;
        User user = new User(userId, null, 1000);
        Concert concert = new Concert(concertId, "test");
        Session session = new Session(sessionId, LocalDateTime.now(), concert);
        Seat seat = new Seat(seatId, 1, 1000, false, session);
        Reservation reservation = new Reservation(reservationId, user, session, seat, seat.getPrice(), ReservationStatus.PENDING);

        given(concertService.getConcert(concertId)).willReturn(concert);
        given(sessionService.getSessionByOpenAndConcertId(concertId, sessionId)).willReturn(session);
        given(seatService.getSeatsForConcertSessionAndAvailable(sessionId, seatId)).willReturn(seat);
        given(userService.getUser(userId)).willReturn(Optional.of(user));
        given(reservationService.addReservation(any(Reservation.class))).willReturn(reservation);

        ReservationResponseDto response = concertFacade.reservation(concertId, sessionId, seatId, userId);

        assertEquals(response.getReservationId(), reservation.getId());
        assertEquals(response.getPrice(), seat.getPrice());

    }





}
