package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.dto.ChargeResponseDto;
import com.hhp.concert.Business.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final UserService userService;


    public ChargeResponseDto charge(Long userId, int amount) {
        User user = userService.chargePoint(userId, amount);

        return new ChargeResponseDto(user.getId(), user.getBalance());
    }
}
