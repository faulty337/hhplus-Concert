package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.Domain.enums.ReservationStatus;
import com.hhp.concert.Business.dto.PaymentResponseDto;
import com.hhp.concert.Business.dto.UserBalanceResponseDto;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final UserService userService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final JwtService jwtService;
    private final QueueService queueService;



    public UserBalanceResponseDto charge(Long userId, int amount) {
        // 충전
        User user = userService.chargePoint(userId, amount);

        return new UserBalanceResponseDto(user.getId(), user.getBalance());
    }


    public UserBalanceResponseDto getBalance(Long userId) {
        //잔액조회
        User user = userService.getUser(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );

        return new UserBalanceResponseDto(user.getId(), user.getBalance());
    }

    @Transactional
    public PaymentResponseDto payment(long userId, long reservationId, String token){
        //토큰 유효성 검사 및 user 유효성 검사
        User user = userService.getUser(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
        if(!jwtService.isProcessing(token, userId)){
            throw new CustomException(ErrorCode.INVALID_TOKEN_STATE);
        };
        if(!queueService.isProcessing(userId)){
            throw new CustomException(ErrorCode.IS_NOT_PROCESSING);
        }


        //예약 정보 확인
        Reservation reservation = reservationService.getReservationByUserId(user.getId(), reservationId);

        Seat seat = reservation.getSeat();

        //잔액 확인
        if(user.getBalance() < seat.getPrice()){
            throw new CustomException(ErrorCode.INSUFFICIENT_FUNDS);
        }

        //결제 정보 저장
        PaymentHistory paymentHistory = paymentService.addPaymentHistory(new PaymentHistory(seat.getPrice(), user, reservation));
        user.userBalance(paymentHistory.getAmount());

        //예약 상태 변환
        reservation.setStatus(ReservationStatus.CONFIRMED);
        Session session = reservation.getSession();

        //처리열 삭제 및 대기열 업데이트
        queueService.removeProcessingByUserId(userId);
        queueService.moveUserToProcessingQueue();

        return new PaymentResponseDto(session.getId(), session.getSessionTime(), seat.getSeatNumber(), paymentHistory.getAmount());
    }
}
