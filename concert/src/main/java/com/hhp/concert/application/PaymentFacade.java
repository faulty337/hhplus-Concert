package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.dto.PaymentResponseDto;
import com.hhp.concert.Business.dto.UserBalanceResponseDto;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.Interfaces.Redis.RedisLock;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final UserService userService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final ConcertService concertService;
    private final WaitingService waitingService;

    private static final Logger logger = LogManager.getLogger(PaymentFacade.class);


    @RedisLock(key = "userId")
    public UserBalanceResponseDto charge(Long userId, int amount) {
        // 충전
        User user = userService.chargePoint(userId, amount);

        return new UserBalanceResponseDto(user.getId(), user.getBalance());
    }


    public UserBalanceResponseDto getBalance(Long userId) {
        //잔액조회
        User user = userService.getUser(userId);

        return new UserBalanceResponseDto(user.getId(), user.getBalance());
    }

    @Transactional
    public PaymentResponseDto payment(long userId, long reservationId){
        // user 유효성 검사
        User user = userService.getUser(userId);

        //예약 정보 확인
        Reservation reservation = reservationService.getReservationByUserId(user.getId(), reservationId);
        Concert concert = concertService.getConcertBySessionId(reservation.getConcertSessionId());
        ConcertSeat concertSeat = concertService.getSeat(concert.getId(), reservation.getConcertSessionId(), reservation.getConcertSeatId());
        ConcertSession concertSession = concertService.getSession(reservation.getConcertSessionId());

        logger.info("balance : {} , amount : {}", user.getBalance(), concertSeat.getPrice());
        //잔액 차감
        userService.usePoint(user.getId(), concertSeat.getPrice());

        //결제 정보 저장
        PaymentHistory paymentHistory = paymentService.addPaymentHistory(new PaymentHistory(concertSeat.getPrice(), user, reservation));

        //예약 상태 변환
        reservationService.confirmReservationStatus(reservationId);

        logger.info("Payment : User ID: {}, Amount : {},", userId, reservation.getReservationPrice());

        //처리열 삭제 및 대기열 업데이트
        waitingService.moveUserToProcessingQueue(userId);

        return new PaymentResponseDto(concertSession.getId(), concertSession.getSessionTime(), concertSeat.getSeatNumber(), paymentHistory.getAmount());
    }
}
