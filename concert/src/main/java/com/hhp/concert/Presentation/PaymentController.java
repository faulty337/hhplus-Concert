package com.hhp.concert.Presentation;

import com.hhp.concert.Business.dto.ChargeRequestDto;
import com.hhp.concert.Business.dto.ChargeResponseDto;
import com.hhp.concert.application.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert")
public class PaymentController {

    private final PaymentFacade paymentFacade;
    @PatchMapping("/charge")
    public ChargeResponseDto charge(
            @RequestBody ChargeRequestDto request
    ){
        return paymentFacade.charge(request.getUserId(), request.getAmount());
    }
}
