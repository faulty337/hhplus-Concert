package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.*;
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
        User user = userService.chargePoint(userId, amount);

        return new UserBalanceResponseDto(user.getId(), user.getBalance());
    }

    public UserBalanceResponseDto getBalance(Long userId) {
        User user = userService.getUser(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );

        return new UserBalanceResponseDto(user.getId(), user.getBalance());
    }

    @Transactional
    public PaymentResponseDto payment(long userId, long reservationId, String token){

        User user = userService.getUser(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
        if(!jwtService.isProcessing(token, userId)){
            throw new CustomException(ErrorCode.INVALID_TOKEN_STATE);
        };


        Reservation reservation = reservationService.getReservationByUserId(user.getId(), reservationId);

        Seat seat = reservation.getSeat();
        if(user.getBalance() < seat.getPrice()){
            throw new CustomException(ErrorCode.INSUFFICIENT_FUNDS);
        }

        PaymentHistory paymentHistory = paymentService.addPaymentHistory(new PaymentHistory(seat.getPrice(), user, reservation));

        Session session = reservation.getSession();




        return new PaymentResponseDto(session.getId(), session.getSessionTime(), seat.getSeatNumber(), paymentHistory.getAmount());
    }
}
