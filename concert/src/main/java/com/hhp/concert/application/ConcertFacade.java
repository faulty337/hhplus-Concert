package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.Domain.event.ReservationEvent;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import com.hhp.concert.Business.dto.GetSessionSeatResponseDto;
import com.hhp.concert.Business.dto.ReservationResponseDto;
import com.hhp.concert.Business.dto.SeatInfoDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ConcertFacade {
    private final ConcertService concertService;
    private final ConcertSessionService concertSessionService;
    private final ConcertSeatService concertSeatService;
    private final UserService userService;
    private final ReservationService reservationService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private static final Logger logger = LogManager.getLogger(ConcertFacade.class);

    @Cacheable(value = "getSessions", key="#concertId")
    public List<GetSessionDateResponseDto> getSessionDate(Long concertId){
        Concert concert = concertService.getConcert(concertId);

        return concertSessionService.getSessionListByOpen(concert.getId()).stream().map(session->new GetSessionDateResponseDto(session.getId(), session.getSessionTime())).toList();
    }


    public GetSessionSeatResponseDto getSessionSeat(Long concertId, Long sessionId) {
        //좌석 정보 반환
        Concert concert = concertService.getConcert(concertId);
        ConcertSession concertSession = concertSessionService.getSessionByOpenAndConcertId(concert.getId(), sessionId);
        List<ConcertSeat> concertSeatList = concertSeatService.getSessionBySeatList(concertSession.getId());

        return new GetSessionSeatResponseDto(concertSession.getSessionTime(), concertSeatList.stream().map(seat -> new SeatInfoDto(seat.getSeatNumber(), seat.isAvailable(), seat.getPrice())).toList());
    }

    @Transactional
    public ReservationResponseDto reserveConcert(Long concertId, Long sessionId, Long seatId, Long userId) {
        User user = userService.getUser(userId);
        //유효성 검사
        ConcertSeat concertSeat = concertService.getAvailableReservationSeats(concertId, sessionId, seatId);

        //통계 · 정보성 logging
        logger.info("Reservation try made: User ID: {}, Concert ID: {}, ConcertSession ID: {}, ConcertSeat ID: {}, Price: {}",
                user.getId(), concertId, sessionId, seatId, concertSeat.getPrice());

        //예약 저장
        Reservation reservation = reservationService.createReservation(new Reservation(user.getId(), concertId, sessionId, concertSeat.getId(), concertSeat.getPrice()));

        ReservationEvent reservationEvent = reservationService.createEvent(reservation.getId());
        applicationEventPublisher.publishEvent(reservationEvent);


        return new ReservationResponseDto(reservation.getId(), reservation.getReservationPrice());
    }
}
