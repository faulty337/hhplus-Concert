package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import com.hhp.concert.Business.dto.GetSessionSeatResponseDto;
import com.hhp.concert.Business.dto.ReservationResponseDto;
import com.hhp.concert.Business.dto.SeatInfoDto;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {
    private final ConcertService concertService;
    private final ConcertSessionService concertSessionService;
    private final ConcertSeatService concertSeatService;
    private final UserService userService;
    private final ReservationService reservationService;

    private static final Logger logger = LogManager.getLogger(ConcertFacade.class);

    @Transactional(readOnly = true)
    public List<GetSessionDateResponseDto> getSessionDate(Long concertId){
        Concert concert = concertService.getConcert(concertId);

        return concertSessionService.getSessionListByOpen(concert.getId()).stream().map(session->new GetSessionDateResponseDto(session.getId(), session.getSessionTime())).toList();
    }

    @Transactional(readOnly = true)
    public GetSessionSeatResponseDto getSessionSeat(Long concertId, Long sessionId) {
        //좌석 정보 반환
        Concert concert = concertService.getConcert(concertId);
        ConcertSession concertSession = concertSessionService.getSessionByOpenAndConcertId(concert.getId(), sessionId);
        List<ConcertSeat> concertSeatList = concertSeatService.getSessionBySeatList(concertSession.getId());

        return new GetSessionSeatResponseDto(concertSession.getSessionTime(), concertSeatList.stream().map(seat -> new SeatInfoDto(seat.getSeatNumber(), seat.isAvailable(), seat.getPrice())).toList());
    }

    @Transactional
    public ReservationResponseDto reservation(Long concertId, Long sessionId, Long seatId, Long userId) {
        User user = userService.getUser(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
        //유효성 검사
        Concert concert = concertService.getConcert(concertId);
        ConcertSession concertSession = concertSessionService.getSessionByOpenAndConcertId(concert.getId(), sessionId);
        ConcertSeat concertSeat = concertSeatService.getSeatsForConcertSessionAndAvailable(concertSession.getId(), seatId);

        //통계 · 정보성 logging
        logger.info("Reservation try made: User ID: {}, Concert ID: {}, ConcertSession ID: {}, ConcertSeat ID: {}, Price: {}",
                user.getId(), concert.getId(), concertSession.getId(), concertSeat.getId(), concertSeat.getPrice());

        //예약 저장
        Reservation reservation = reservationService.addReservation(new Reservation(user, concertSession, concertSeat, concertSeat.getPrice()));


        return new ReservationResponseDto(reservation.getId(), reservation.getReservationPrice());
    }
}
