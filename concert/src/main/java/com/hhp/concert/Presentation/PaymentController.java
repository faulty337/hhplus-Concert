package com.hhp.concert.Presentation;

import com.hhp.concert.Business.dto.ChargeRequestDto;
import com.hhp.concert.Business.dto.PaymentRequestDto;
import com.hhp.concert.Business.dto.PaymentResponseDto;
import com.hhp.concert.Business.dto.UserBalanceResponseDto;
import com.hhp.concert.application.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    //충전
    @PatchMapping("/charge")
    public UserBalanceResponseDto charge(
            @RequestBody ChargeRequestDto request
    ){
        return paymentFacade.charge(request.getUserId(), request.getAmount());
    }

    //잔액조회
    @GetMapping("/balance")
    public UserBalanceResponseDto getBalance(
            @RequestBody Long userId
    ){
        return paymentFacade.getBalance(userId);
    }

    //결제
    @PostMapping("/payment")
    public PaymentResponseDto concertPayment(
            @RequestBody PaymentRequestDto request
    ){

        return paymentFacade.payment(request.getUserId(), request.getReservationId());
    }
}
