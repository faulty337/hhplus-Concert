package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.Business.dto.GetSessionDateResponseDto;
import com.hhp.concert.Business.dto.GetSessionSeatResponseDto;
import com.hhp.concert.Business.dto.ReservationResponseDto;
import com.hhp.concert.Business.dto.SeatInfoDto;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import com.hhp.concert.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {
    private final ConcertService concertService;
    private final SessionService sessionService;
    private final SeatService seatService;
    private final UserService userService;
    private final ReservationService reservationService;
    private final JwtService jwtService;


    @Transactional(readOnly = true)
    public List<GetSessionDateResponseDto> getSessionDate(Long concertId){
        Concert concert = concertService.getConcert(concertId);

        return sessionService.getSessionListByOpen(concert.getId()).stream().map(session->new GetSessionDateResponseDto(session.getId(), session.getSessionTime())).toList();
    }

    @Transactional(readOnly = true)
    public GetSessionSeatResponseDto getSessionSeat(Long concertId, Long sessionId) {
        //좌석 정보 반환
        Concert concert = concertService.getConcert(concertId);
        Session session = sessionService.getSessionByOpenAndConcertId(concert.getId(), sessionId);
        List<Seat> seatList = seatService.getSessionBySeatList(session.getId());

        return new GetSessionSeatResponseDto(session.getSessionTime(), seatList.stream().map(seat -> new SeatInfoDto(seat.getSeatNumber(), seat.isAvailable(), seat.getPrice())).toList());
    }

    @Transactional
    public ReservationResponseDto reservation(Long concertId, Long sessionId, Long seatId, String token) {
        //JWT 토큰 이용 유효 시간, user sign 확인
        Long userId = jwtService.extractUserId(token);
        User user = userService.getUser(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
        if(!jwtService.isProcessing(token, userId)){
            throw new CustomException(ErrorCode.INVALID_TOKEN_STATE);
        };

        //유효성 검사
        Concert concert = concertService.getConcert(concertId);
        Session session = sessionService.getSessionByOpenAndConcertId(concert.getId(), sessionId);
        Seat seat = seatService.getSeatsForConcertSessionAndAvailable(session.getId(), seatId);

        //예약 저장
        Reservation reservation = reservationService.addReservation(new Reservation(user, session, seat, seat.getPrice()));


        return new ReservationResponseDto(reservation.getId(), reservation.getReservationPrice());
    }
}
