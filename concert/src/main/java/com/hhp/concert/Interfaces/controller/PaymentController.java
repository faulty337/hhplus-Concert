package com.hhp.concert.Interfaces.controller;

import com.hhp.concert.Business.dto.ChargeRequestDto;
import com.hhp.concert.Business.dto.PaymentRequestDto;
import com.hhp.concert.Business.dto.PaymentResponseDto;
import com.hhp.concert.Business.dto.UserBalanceResponseDto;
import com.hhp.concert.application.PaymentFacade;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert")
public class PaymentController {

    private final PaymentFacade paymentFacade;
    @PatchMapping("/charge")
    public UserBalanceResponseDto charge(
            @RequestBody ChargeRequestDto request
    ){
        return paymentFacade.charge(request.getUserId(), request.getAmount());
    }

    @GetMapping("/balance")
    public UserBalanceResponseDto getBalance(
            @RequestParam Long userId
    ){
        return paymentFacade.getBalance(userId);
    }

    @PostMapping("/payment")
    public PaymentResponseDto concertPayment(
            @RequestBody PaymentRequestDto request
    ){
        try{
            return paymentFacade.payment(request.getUserId(), request.getReservationId());
        }catch (ObjectOptimisticLockingFailureException e){
            throw new CustomException(ErrorCode.NOT_AVAILABLE_SEAT);
        }
    }
}
